package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String stackTrace;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
