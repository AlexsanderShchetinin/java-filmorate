package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    // методы добавления и удаления лайков
    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getAll();

    Film getById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    /**
     * showPopularFilms возвращает "amount" самых популярных фильмов по количеству лайков
     *
     * @param amount -> количество возвращаемых фильмов
     */
    Collection<Film> showPopularFilms(int amount);

    List<Genre> getAllGenres();

    Genre getGenre(int genreId);

    List<Mpa> getAllMpas();

    Mpa getMpa(int ratingId);

}
