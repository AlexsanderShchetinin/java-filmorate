package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dto.GenresFilm;
import ru.yandex.practicum.filmorate.exceptoin.DbException;
import ru.yandex.practicum.filmorate.exceptoin.LogicalException;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;

    private MapSqlParameterSource getFilmParams(Film film) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValues(Map.of("film_name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "rating_id", film.getMpa().getId()));
        return map;
    }

    private List<Genre> checkDbGenres(Film film) {
        // Проверяем наличие передаваемых жанров в таблице genres
        if (film.getGenres() != null) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();
            List<Genre> genres = getGenresByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new LogicalException("not found all genres included in film");
            }
            return genres;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        // проверяем корректность передаваемых данных в БД по переменным Genre
        List<Genre> returnedGenres = checkDbGenres(film);

        // добавляем запись с фильмом в таблицу films
        MapSqlParameterSource params = getFilmParams(film);
        String sqlCreateFilm = "INSERT INTO films(film_name, description, release_date, duration, rating_id) " +
                "VALUES (:film_name, :description, :release_date, :duration, :rating_id)";
        jdbc.update(sqlCreateFilm, params, keyHolder, new String[]{"film_id"});
        film.setId(keyHolder.getKeyAs(Long.class));

        // добавляем записи с жанрами фильмов в таблицу genres_film
        String sqlInsertGenresFilm = "INSERT INTO genres_film(film_id, genre_id) VALUES (:filmId, :genreId)";
        for (Genre genre : returnedGenres) {
            jdbc.update(sqlInsertGenresFilm,
                    Map.of("filmId", film.getId(), "genreId", genre.getId()));
        }

        // заполняем жанры и рейтинг из таблиц в возвращаемый объект фильма
        film.setGenres(new LinkedHashSet<>((returnedGenres)));
        // MPA всегда будет задано (если не задано то в методе checkDbMpa устанавливается id=1 по умолчанию)
        film.setMpa(getMpa(film.getMpa().getId()).get());  // добавляем наименование MPA в  класс dto
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        // Проверяем что запись есть в таблице films
        if (get(newFilm.getId()).isEmpty()) {
            throw new NotFoundException("film with id=" + newFilm.getId() + " not exist in database");
        }

        // проверяем корректность передаваемых данных в БД по переменным Genre
        List<Genre> returnedGenres = checkDbGenres(newFilm);

        // Выполняем запрос на обновление в БД
        String sqlUpdate = "MERGE INTO films(film_id, film_name, description, release_date, duration, rating_id)" +
                " VALUES (:film_id, :film_name, :description, :release_date, :duration, :rating_id)";
        MapSqlParameterSource params = getFilmParams(newFilm);
        params.addValues(Map.of("film_id", newFilm.getId()));
        if (jdbc.update(sqlUpdate, params) != 1) {
            throw new DbException("Не удалось обновить данные в БД: таблица films для film.id=" + newFilm.getId());
        }
        // заполняем жанры и рейтинг из таблиц в возвращаемый объект фильма
        newFilm.setGenres(new HashSet<>((returnedGenres)));
        // MPA всегда будет задано (если не задано то в методе checkDbMpa устанавливается id=1 по умолчанию)
        newFilm.setMpa(getMpa(newFilm.getMpa().getId()).get());  // добавляем наименование MPA в  класс dto
        return newFilm;
    }

    @Override
    public List<Film> getAll() {
        // получаем все жанры
        List<Genre> allGenres = getAllGenres();

        // получаем все фильмы (без жанров)
        List<Film> films = jdbc.query("SELECT * FROM films JOIN mpas ON films.rating_id=mpas.rating_id",
                new FilmRowMapper());

        // Получаем связи фильм-жанры
        List<GenresFilm> genresFilms = jdbc.query("SELECT * FROM genres_film", new RowMapper<GenresFilm>() {
            @Override
            public GenresFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new GenresFilm(rs.getLong("film_id"), rs.getInt("genre_id"));
            }
        });

        // Объединяем жанры в фильмы
        for (Film film : films) {
            List<Genre> genresFilm = genresFilms.stream()
                    .filter(genresFilm1 -> genresFilm1.filmId() == film.getId())
                    .map(genresFilm1 -> allGenres.get(genresFilm1.genreId() - 1))
                    .toList();
            film.setGenres(new HashSet<>(genresFilm));
        }
        return films;
    }

    @Override
    public List<Film> getByIds(LinkedList<Long> ids) {
        // получаем все жанры
        List<Genre> allGenres = getAllGenres();

        // получаем все фильмы по id(без жанров)
        MapSqlParameterSource filmParams = new MapSqlParameterSource();
        StringBuilder sbQueryFilms = new StringBuilder("SELECT * FROM films JOIN mpas ON films.rating_id=mpas.rating_id " +
                "WHERE films.film_id IN (");
        for (Long id : ids) {
            sbQueryFilms.append(":id").append(id).append(", ");
            filmParams.addValues(Map.of("id" + id, id));
        }
        sbQueryFilms.deleteCharAt(sbQueryFilms.length() - 2); // удаляем последнюю запятую
        sbQueryFilms.append(")");
        List<Film> films = jdbc.query(sbQueryFilms.toString(), filmParams, new FilmRowMapper());

        // Получаем связи фильм-жанры
        MapSqlParameterSource genreParams = new MapSqlParameterSource();
        StringBuilder sbQueryGenres = new StringBuilder("SELECT * FROM genres_film WHERE film_id IN (");
        for (Long id : ids) {
            sbQueryGenres.append(":id").append(id).append(", ");
            genreParams.addValues(Map.of("id" + id, id));
        }
        sbQueryGenres.deleteCharAt(sbQueryGenres.length() - 2); // удаляем последнюю запятую
        sbQueryGenres.append(")");
        List<GenresFilm> genresFilms = jdbc.query(sbQueryGenres.toString(), genreParams, new RowMapper<GenresFilm>() {
            @Override
            public GenresFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new GenresFilm(rs.getLong("film_id"), rs.getInt("genre_id"));
            }
        });

        // Объединяем жанры в фильмы
        for (Film film : films) {
            List<Genre> genresFilm = genresFilms.stream()
                    .filter(genresFilm1 -> genresFilm1.filmId() == film.getId())
                    .map(genresFilm1 -> allGenres.get(genresFilm1.genreId() - 1))
                    .toList();
            film.setGenres(new HashSet<>(genresFilm));
        }
        return films;
    }

    @Override
    public Optional<Film> get(long id) {
        String queryFilm = "SELECT * FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "LEFT JOIN (SELECT gf.film_id, genres.genre_id, genres.genre_name " +
                "FROM genres_film AS gf " +
                "JOIN genres ON genres.genre_id = gf.genre_id " +
                "WHERE gf.film_id=:film_id) AS g ON f.film_id = g.film_id " +
                "WHERE f.film_id=:film_id";
        final Film film = jdbc.query(queryFilm, Map.of("film_id", id), new FilmResultSetExtractor());
        return Optional.ofNullable(film);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query("SELECT * FROM genres ORDER BY genre_id", new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        String queryGenre = "SELECT * FROM genres WHERE genre_id = :genre_id";
        final List<Genre> genres = jdbc.query(queryGenre, Map.of("genre_id", genreId), new GenreRowMapper());
        return genres.stream().findFirst();
    }


    private List<Genre> getGenresByIds(List<Integer> genreIds) {
        StringBuilder sqlBaseQuery = new StringBuilder("SELECT * FROM genres WHERE genre_id IN (");
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (Integer genreId : genreIds) {
            String additionalParamsSQL = ":genreId" + genreId + ", ";
            sqlBaseQuery.append(additionalParamsSQL);
            params.addValues(Map.of("genreId" + genreId, genreId));
        }
        sqlBaseQuery.deleteCharAt(sqlBaseQuery.length() - 2); // удаляем последнюю запятую
        sqlBaseQuery.append(") ORDER BY genre_id");  // добавляем сортировку по id
        return jdbc.query(sqlBaseQuery.toString(), params, new GenreRowMapper());
    }

    @Override
    public List<Mpa> getAllMpas() {
        return jdbc.query("SELECT * FROM mpas", new RatingRowMapper());
    }

    @Override
    public Optional<Mpa> getMpa(int ratingId) {
        String queryRating = "SELECT * FROM mpas WHERE rating_id = :rating_id";
        final List<Mpa> mpas = jdbc.query(queryRating, Map.of("rating_id", ratingId), new RatingRowMapper());
        return mpas.stream().findFirst();
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlUpdate = "MERGE INTO likes_film(film_id, user_id) VALUES (:filmId, :userId);";
        jdbc.update(sqlUpdate, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlDelete = "DELETE FROM likes_film WHERE film_id=:filmId AND user_id=:userId";
        jdbc.update(sqlDelete, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public Collection<Film> showPopularFilms(int count) {
        String query = "SELECT f.*, m.*, COUNT(user_id) AS likes " +
                "FROM films f " +
                "LEFT JOIN likes_film lf ON f.film_id = lf.film_id " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes DESC " +
                "LIMIT :count";
        final List<Film> popularFilm = jdbc.query(query, Map.of("count", count), new FilmRowMapper());
        // LinkedList<Film> linkedList = new LinkedList<>(popularFilmIds);

        // Получаем связи фильм-жанры
        MapSqlParameterSource genreParams = new MapSqlParameterSource();
        StringBuilder sbQueryGenres = new StringBuilder("SELECT * FROM genres_film WHERE film_id IN (");
        for (Film film : popularFilm) {
            sbQueryGenres.append(":id").append(film.getId()).append(", ");
            genreParams.addValues(Map.of("id" + film.getId(), film.getId()));
        }
        sbQueryGenres.deleteCharAt(sbQueryGenres.length() - 2); // удаляем последнюю запятую
        sbQueryGenres.append(")");
        List<GenresFilm> genresFilms = jdbc.query(sbQueryGenres.toString(), genreParams, new RowMapper<GenresFilm>() {
            @Override
            public GenresFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new GenresFilm(rs.getLong("film_id"), rs.getInt("genre_id"));
            }
        });
        // получаем все жанры
        List<Genre> allGenres = getAllGenres();
        // Объединяем жанры в фильмы
        for (Film film : popularFilm) {
            List<Genre> genresFilm = genresFilms.stream()
                    .filter(genresFilm1 -> genresFilm1.filmId() == film.getId())
                    .map(genresFilm1 -> allGenres.get(genresFilm1.genreId() - 1))
                    .toList();
            film.setGenres(new HashSet<>(genresFilm));
        }
        return popularFilm;
    }

}




