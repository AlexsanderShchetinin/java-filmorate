package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.Marker;
import ru.yandex.practicum.filmorate.validator.ReleaseDateLimitation;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
    @Null(groups = Marker.Create.class, message = "При создании фильма id должно быть null.")
    @NotNull(groups = Marker.Update.class, message = "При обновлении фильма id не должно быть null.")
    private Long id;

    @NotBlank(message = "Имя фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание не должно быть больше 200 символов.")
    private String description;

    @ReleaseDateLimitation    // аннотация проверяет что переменная LocalDate не раньше 28.12.1895 и не равна null
    private LocalDate releaseDate;    // дата выхода фильма

    @Positive(message = "Продолжительность фильма не может быть отрицательна или = 0")
    private int duration;    // Продолжительность в минутах

    @JsonIgnore
    private Set<Long> likesId;    // Множество с id пользователей, поставивших лайк

}
