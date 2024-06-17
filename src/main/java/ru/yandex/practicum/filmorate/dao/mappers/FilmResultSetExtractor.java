package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FilmResultSetExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
        Film film = null;
        Set<Genre> genreSet = new HashSet<>();
        while (rs.next()) {
            if (film == null) {
                film = Film.builder()
                        .id(rs.getLong("films.film_id"))
                        .name(rs.getString("films.film_name"))
                        .description(rs.getString("films.description"))
                        .releaseDate(rs.getDate("films.release_date").toLocalDate())
                        .duration(rs.getInt("films.duration"))
                        .mpa(new Mpa(rs.getInt("mpas.rating_id"),
                                rs.getString("mpas.rating_name")))
                        .build();
            }
            if (rs.getInt("g.genre_id") != 0) {
                genreSet.add(new Genre(rs.getInt("g.genre_id"),
                        rs.getString("g.genre_name")));
            }

        }
        if (film != null) {
            film.setGenres(genreSet);
        }
        return film;
    }
}
