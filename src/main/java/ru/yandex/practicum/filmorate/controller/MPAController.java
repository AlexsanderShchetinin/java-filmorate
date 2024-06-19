package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MPAController {
    private final FilmService filmServiceJdbcImpl;

    // Получение всех жанров
    @GetMapping
    public List<Mpa> getAll() {
        log.info("==> GET /MPAs <==");
        return filmServiceJdbcImpl.getAllMpas();
    }

    // Получение жанра по идентификатору
    @GetMapping("/{id}")
    public Mpa getById(@PathVariable int id) {
        log.info("GET MPA by id={}", id);
        return filmServiceJdbcImpl.getMpa(id);
    }

}
