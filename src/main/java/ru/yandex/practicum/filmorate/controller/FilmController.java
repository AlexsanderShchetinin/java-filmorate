package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Marker;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> filmRepository = new HashMap<>();
    private long counter = 0L;

    private long getNextId() {
        counter++;
        return counter;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("==> GET /films <==");
        return filmRepository.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public Film create(@RequestBody @Valid Film film) {
        // при создании фильма устанавливаем для его id
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        filmRepository.put(film.getId(), film);
        log.info("Добавлен фильм {}  с id={}", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public Film update(@RequestBody @Valid Film newFilm) throws RuntimeException {
        if (filmRepository.containsKey(newFilm.getId())) {
            // меняем фильм в таблице
            filmRepository.put(newFilm.getId(), newFilm);
            log.info("фильм {}  с id={} обновлен.", newFilm.getName(), newFilm.getId());
            return newFilm;
        }
        // если фильм по id не найден выбрасвываем исключение
        log.warn("фильм с id ={} не найден", newFilm.getId());
        throw new RuntimeException("фильм с id = " + newFilm.getId() + " не найден");
    }
}
