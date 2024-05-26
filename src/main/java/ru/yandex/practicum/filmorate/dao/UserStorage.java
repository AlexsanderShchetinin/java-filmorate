package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserStorage {

    Map<Long, User> repository = new HashMap<>();
    long counter = 0;

    User add(User user);

    User update(User newUser);

    Collection<User> getAll();

    Optional<User> get(long id);

    Collection<User> getFriends(Set<Long> friendIds);

}
