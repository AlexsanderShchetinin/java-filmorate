package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmServiceJdbcImpl;

    // Получение всех жанров
    @GetMapping
    public List<Genre> getAll() {
        log.info("==> GET /genres <==");
        return filmServiceJdbcImpl.getAllGenres();
    }

    // Получение жанра по идентификатору
    @GetMapping("/{id}")
    public Genre getById(@PathVariable int id) {
        log.info("GET genre by id={}", id);
        return filmServiceJdbcImpl.getGenre(id);
    }

}
