package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmServiceJdbcImpl;

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") long id) {
        log.info("==> GET /film by id={} <==", id);
        return filmServiceJdbcImpl.getById(id);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("==> GET /films <==");
        return filmServiceJdbcImpl.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public Film create(@RequestBody @Valid Film film) {
        log.info("Create Film: {} <-- STARTED", film.getName());
        Film createdFilm = filmServiceJdbcImpl.create(film);
        log.info("Film: {} <-- CREATED", createdFilm.getName());
        return createdFilm;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Update Film: {} <-- STARTED", newFilm.getName());
        Film updetedFilm = filmServiceJdbcImpl.update(newFilm);
        log.info("Film: {} with id={} <-- UPDATED", newFilm.getName(), newFilm.getId());
        return updetedFilm;
    }

    //{userId} добавляет лайк фильму с {id}
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Try to add a like FilmId={} , userId={} <-- STARTED", filmId, userId);
        Film film = filmServiceJdbcImpl.addLike(filmId, userId);
        log.info("Add a like to Film: {} , userId={} <-- COMPLETED", film.getName(), userId);
    }

    //{userId} удаляет лайк фильму с {id}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Try to remove a like FilmId={} , userId={} <-- STARTED", filmId, userId);
        Film film = filmServiceJdbcImpl.removeLike(filmId, userId);
        log.info("Remove a like to Film: {} , userId={} <-- COMPLETED", film.getName(), userId);
    }

    // получения {count} популярных фильмов по кол-ву лайков (count=10 если его не указали)
    @GetMapping("/popular")
    public Collection<Film> showPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Try to show {} popular films <-- STARTED", count);
        Collection<Film> films = filmServiceJdbcImpl.showPopularFilms(count);
        log.info("Show {} popular films <-- COMPLETED", count);
        return films;
    }

}
