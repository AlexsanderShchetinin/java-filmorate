package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }


    @GetMapping
    public Collection<Film> getAll() {
        log.info("==> GET /films <==");
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public Film create(@RequestBody @Valid Film film) {
        log.info("Create Film: {} <-- STARTED", film.getName());
        Film createdFilm = service.create(film);
        log.info("Film: {} <-- CREATED", createdFilm.getName());
        return createdFilm;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Update Film: {} <-- STARTED", newFilm.getName());
        Film updetedFilm = service.update(newFilm);
        log.info("Film: {} with id={} <-- UPDATED", newFilm.getName(), newFilm.getId());
        return updetedFilm;
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Try to add a like FilmId={} , userId={} <-- STARTED", filmId, userId);
        Film film = service.addLike(filmId, userId);
        log.info("Add a like to Film: {} , userId={} <-- COMPLETED", film.getName(), userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Try to remove a like FilmId={} , userId={} <-- STARTED", filmId, userId);
        Film film = service.removeLike(filmId, userId);
        log.info("Remove a like to Film: {} , userId={} <-- COMPLETED", film.getName(), userId);
    }

    @GetMapping("/popular")
    public Collection<Film> showPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Try to show {} popular films <-- STARTED", count);
        Collection<Film> films = service.showPopularFilms(count);
        log.info("Show {} popular films <-- COMPLETED", count);
        return films;
    }


}
