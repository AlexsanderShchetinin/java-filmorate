package ru.yandex.practicum.filmorate.exceptoin;

public class DbException extends RuntimeException {
    public DbException(String message) {
        super(message);
    }
}
