package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Service("userServiceJdbcImpl")
@RequiredArgsConstructor
public class UserServiceJdbcImpl implements UserService {

    private final UserStorage userDbStorage;

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userDbStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        User friend = userDbStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with id=" + friendId));
        userDbStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = userDbStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        User friend = userDbStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with id=" + friendId));
        userDbStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getAll() {
        return userDbStorage.getAll();
    }

    @Override
    public User create(User user) {
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userDbStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        return userDbStorage.update(newUser);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        userDbStorage.get(id)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + id));
        userDbStorage.get(otherId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + otherId));
        return userDbStorage.getCommonFriends(id, otherId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        userDbStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        return userDbStorage.getFriends(userId);
    }
}
