package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.Set;

/**
 * User.
 */
@Data
@Builder
@EqualsAndHashCode(of = "id")
public class User {
    @Null(groups = Marker.Create.class, message = "При создании пользователя id должно быть null.")
    @NotNull(groups = Marker.Update.class, message = "id пользователя не должно быть null.")
    private Long id;

    @Email(message = "email не соответствует стандарту электронного почтового адреса")
    private String email;

    @NotBlank(message = "логин пользователя не может быть пустым")
    private String login;

    private String name;

    @Past(message = "День рождения пользователя должен находиться в прошедшем периоде времени")
    @NotNull(message = "Поле birthday должно быть заполнено")
    private LocalDate birthday;

    @JsonIgnore
    private Set<Long> friendIds;

}


