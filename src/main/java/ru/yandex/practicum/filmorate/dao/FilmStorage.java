package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {


    Film create(Film film);

    Film update(Film newFilm);

    List<Film> getAll();

    List<Film> getByIds(LinkedList<Long> ids);

    Optional<Film> get(long id);


    List<Genre> getAllGenres();

    Optional<Genre> getGenre(int genreId);


    List<Mpa> getAllMpas();

    Optional<Mpa> getMpa(int ratingId);


    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);


    Collection<Film> showPopularFilms(int count);

}
