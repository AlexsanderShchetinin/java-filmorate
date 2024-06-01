package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * User.
 */
@Data
@Builder
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
    private Map<Long, FriendStatus> friendsId = new HashMap<>();    // мапа для хранения id друзей и статуса дружбы

}


