package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User[] addFriend(Long userId, Long friendId);

    User[] removeFriend(Long userId, Long friendId);

    Collection<User> getAll();

    User create(User user);

    User update(User newUser);

    Collection<User> getCommonFriends(Long id, Long otherId);

    Collection<User> getFriends(Long userId);
}
