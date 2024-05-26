package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Map<Long, Film> repository = new HashMap<>();
    long counter = 0;

    Film add(Film film);

    Film update(Film newFilm);

    Collection<Film> getAll();

    Optional<Film> get(long id);


}
