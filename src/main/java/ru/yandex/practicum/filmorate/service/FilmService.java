package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    // методы добавления и удаления лайков
    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film newFilm);

    /**
     * showPopularFilms возвращает "amount" самых популярных фильмов по количеству лайков
     *
     * @param amount -> количество возвращаемых фильмов
     */
    Collection<Film> showPopularFilms(int amount);
}
