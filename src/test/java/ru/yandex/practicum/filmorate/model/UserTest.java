package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.converter.DateFormat;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест класса User")
class UserTest {
    private static Validator validator;
    private User validUser;
    private User emptyUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        validUser = User.builder()
                .name("Developer")
                .login("Dev4j")
                .email("Dev@yandex.ru")
                .birthday(LocalDate.parse("1997-11-01", DateFormat.DATE_FORMAT_1))
                .build();
        emptyUser = User.builder().build();
    }

    @DisplayName("Валидация пользователя с корректными полями")
    @Test
    void createValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty(), "Тест корректного создания экземпляра User провален");
    }

    @DisplayName("Валидация пользователя с пустыми полями")
    @Test
    void createInvalidEmptyUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(emptyUser);
        assertFalse(violations.isEmpty(), "Тест валидации экземпляра User с пустыми полями провален");
    }

    @DisplayName("Валидация пользователя с некорректным email")
    @Test
    void createInvalidUserWithEmail() {
        validUser.setEmail(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("email не соответствует стандарту электронного почтового адреса",
                    violation.getMessage(), "Валидация пустого email не сработала");
        }

        validUser.setEmail("это-неправильный?эмейл@");
        Set<ConstraintViolation<User>> violations2 = validator.validate(validUser);
        for (ConstraintViolation<User> violation : violations2) {
            assertEquals("email не соответствует стандарту электронного почтового адреса",
                    violation.getMessage(), "Валидация неправильного email не сработала");
        }

        validUser.setEmail(null);
        Set<ConstraintViolation<User>> violations3 = validator.validate(validUser);
        for (ConstraintViolation<User> violation : violations3) {
            assertEquals("email не соответствует стандарту электронного почтового адреса",
                    violation.getMessage(), "Валидация email=null не сработала");
        }

    }

    @DisplayName("Валидация пользователя с некорректным логином")
    @Test
    void createInvalidUserWithLogin() {
        validUser.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty(), "Тест валидации экземпляра User с login=null провален");

        validUser.setLogin("   ");
        Set<ConstraintViolation<User>> violations2 = validator.validate(validUser);
        for (ConstraintViolation<User> violation2 : violations2) {
            assertEquals("логин пользователя не может быть пустым", violation2.getMessage(),
                    "Тест валидации экземпляра User с пустым логином провален");
        }
    }

    @DisplayName("Валидация пользователя с некорректной датой рождения")
    @Test
    void createInvalidUserWithBirthday() {
        validUser.setBirthday(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty(), "Тест валидации экземпляра User с полем birthday=null провален");

        validUser.setBirthday(LocalDate.parse("2999-01-01"));
        Set<ConstraintViolation<User>> violations2 = validator.validate(validUser);
        for (ConstraintViolation<User> violation2 : violations2) {
            assertEquals("День рождения пользователя должен находиться в прошедшем периоде времени",
                    violation2.getMessage(),
                    "Тест валидации экземпляра User с полем birthday из будущего провален");
        }

    }

}