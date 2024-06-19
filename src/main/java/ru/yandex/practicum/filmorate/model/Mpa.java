package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * расшифровка имен, хранимых в БД (поставлено ограничение на уровне БД):
 * G - у фильма нет возрастных ограничений,
 * PG - детям рекомендуется смотреть фильм с родителями,
 * PG-13 - детям до 13 лет просмотр не желателен,
 * R - лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
 * NC-17 - лицам до 18 лет просмотр запрещён.
 */
@Data
@EqualsAndHashCode(of = "id")
@Builder
@RequiredArgsConstructor
public class Mpa {
    private final int id;
    private final String name;
}
