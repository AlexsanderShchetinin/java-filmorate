package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Проверка на запись на уровне БД след значений:
 * COMEDY,
 * DRAMA,
 * CARTOON,
 * THRILLER,
 * DOCUMENTARY,
 * ACTION
 */

@Data
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Genre {
    private final int id;
    private final String name;

}