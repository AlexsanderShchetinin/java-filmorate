package ru.yandex.practicum.filmorate.exceptoin;

public class NoContentException extends RuntimeException {
    public NoContentException(String message) {
        super(message);
    }
}
