package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateLimitation {
    String message() default "Дата выхода фильма не может быть раньше 1895 года";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
