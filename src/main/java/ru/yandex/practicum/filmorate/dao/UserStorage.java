package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {


    User create(User user);

    User update(User newUser);

    List<User> getAll();

    Optional<User> get(long id);


    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherUserId);

}
