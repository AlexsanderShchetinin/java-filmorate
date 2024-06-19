package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptoin.LogicalException;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

@Service("filmServiceJdbcImpl")
@RequiredArgsConstructor
public class FilmServiceJdbcImpl implements FilmService {

    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;

    @Override
    public Film addLike(long filmId, long userId) {
        // проверка на наличие фильма и пользователя в БД
        Film film = filmDbStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found."));
        userDbStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        // добавляем в БД запись с лайком к фильму
        filmDbStorage.addLike(filmId, userId);
        return film;
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        Film film = filmDbStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found."));
        userDbStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        filmDbStorage.removeLike(filmId, userId);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return filmDbStorage.getAll();
    }

    @Override
    public Film getById(long id) {
        return filmDbStorage.get(id)
                .orElseThrow(() -> new NotFoundException("film with id=" + id + " not found"));
    }

    @Override
    public Film create(Film film) {
        checkDbMpa(film);
        return filmDbStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        checkDbMpa(newFilm);
        return filmDbStorage.update(newFilm);
    }

    // проверка на корректность рейтинга в передаваемом фильме
    private void checkDbMpa(Film film) {
        // при отсутствии MPA устанавливаем значение по умолчанию=1 (без ограничения "G")
        // в data.sql обязательно это значение должно совпадать!
        if (film.getMpa() == null) {
            film.setMpa(new Mpa(1, "G"));
        }
        // проверяем наличие id MPA передаваемое в фильме в таблице БД mpas
        List<Mpa> mpas = getAllMpas();
        long count = mpas.stream()
                .filter(mpa -> film.getMpa().getId() == mpa.getId())
                .count();
        if (count == 0) {
            throw new LogicalException("film MPA with id=" + film.getMpa().getId() + " does not exist");
        }

    }

    @Override
    public Collection<Film> showPopularFilms(int amount) {
        return filmDbStorage.showPopularFilms(amount);
    }

    @Override
    public List<Genre> getAllGenres() {
        return filmDbStorage.getAllGenres();
    }

    @Override
    public Genre getGenre(int genreId) {
        return filmDbStorage.getGenre(genreId)
                .orElseThrow(() -> new NotFoundException("genre with genreId=" + genreId + " not found"));
    }

    @Override
    public List<Mpa> getAllMpas() {
        return filmDbStorage.getAllMpas();
    }

    @Override
    public Mpa getMpa(int ratingId) {
        return filmDbStorage.getMpa(ratingId)
                .orElseThrow(() -> new NotFoundException("rating with ratingId=" + ratingId + " not found"));
    }
}
