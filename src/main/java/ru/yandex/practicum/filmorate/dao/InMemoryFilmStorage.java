package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> repository;
    private long counter;


    public InMemoryFilmStorage() {
        this.repository = new HashMap<>();
        this.counter = 0L;
    }

    public Collection<Film> getAll() {
        return repository.values();
    }

    @Override
    public Optional<Film> get(long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public Film add(Film film) {
        // при создании фильма устанавливаем для его id
        film.setId(++counter);
        // сохраняем новый фильм в памяти приложения
        repository.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        // меняем фильм в таблице
        repository.put(newFilm.getId(), newFilm);
        return newFilm;
    }


}
