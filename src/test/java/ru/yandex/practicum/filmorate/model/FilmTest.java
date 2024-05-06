package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.converter.DateFormat;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест класса Film")
class FilmTest {

    private static Validator validator;
    private Film validFilm;
    private Film emptyFilm;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        validFilm = Film.builder()
                .name("Titanic")
                .description("someFilm")
                .duration(194)
                .releaseDate(LocalDate.parse("1997-11-01", DateFormat.DATE_FORMAT_1))
                .build();
        emptyFilm = Film.builder().build();
    }

    @DisplayName("Валидация фильма с корректными полями")
    @Test
    void createValidFilm() {
        Set<ConstraintViolation<Film>> violation = validator.validate(validFilm);
        assertTrue(violation.isEmpty(), "Тест корректного создания экземпляра Film провален");
    }

    @DisplayName("Валидация фильма с пустыми полями")
    @Test
    void createInvalidEmptyFilm() {
        Set<ConstraintViolation<Film>> violation = validator.validate(emptyFilm);
        assertFalse(violation.isEmpty(), "Тест некорректного создания экземпляра Film провален");
    }

    @DisplayName("Валидация фильма с некорректным именем name")
    @Test
    @Validated
    void createInvalidFilmWithName() {
        validFilm.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Валидация с пустым именем фильма не сработала");
        validFilm.setName(null);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(validFilm);
        assertFalse(violations2.isEmpty(), "Валидация с null именем фильма name не сработала");
    }

    @DisplayName("Валидация фильма с некорректным описанием description")
    @Test
    void createInvalidFilmWithDescription() {
        validFilm.setDescription("Descriptoin more than 200 letters, someText...someText...someText...someText..." +
                "someText...someText...someText...someText...someText...someText...someText...someText...someText..." +
                "someText...someText...someText...someText...someText...someText...someText...someText...someText..." +
                "someText...someText...someText...someText...someText...someText...someText...someText...someText...");

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Валидация с описанием фильма discription>200 символов не сработала");

        for (ConstraintViolation<Film> violation : violations) {
            assertEquals("Описание не должно быть больше 200 символов.", violation.getMessage(),
                    "Неверный ответ валидации description при size()>200");
        }
    }

    @DisplayName("Валидация фильма с некорректной датой релиза")
    @Test
    void createInvalidFilmWithReleaseDate() {
        validFilm.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Валидация с null датой релиза не сработала");

        validFilm.setReleaseDate(LocalDate.parse("1895-11-01", DateFormat.DATE_FORMAT_1));
        Set<ConstraintViolation<Film>> violations2 = validator.validate(validFilm);
        assertFalse(violations2.isEmpty(), "Валидация с датой релиза ранее 28.12.1985 не сработала");

        validFilm.setReleaseDate(LocalDate.parse("1895-12-28", DateFormat.DATE_FORMAT_1));
        Set<ConstraintViolation<Film>> violations3 = validator.validate(validFilm);
        assertTrue(violations3.isEmpty(), "Валидация с датой релиза ранее 28.12.1985 не сработала");

    }

    @DisplayName("Валидация фильма с некорректной продолжительностью duration")
    @Test
    void createInvalidFilmWithDuration() {
        validFilm.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Валидация с нулевой продолжительностью фильма не сработала");

        validFilm.setDuration(-100);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(validFilm);
        assertFalse(violations2.isEmpty(), "Валидация с отрицательной продолжительностью фильма не сработала");

        validFilm.setDuration(100);
        Set<ConstraintViolation<Film>> violations3 = validator.validate(validFilm);
        assertTrue(violations3.isEmpty(), "Валидация с положительной продолжительностью фильма не сработала");
    }

}