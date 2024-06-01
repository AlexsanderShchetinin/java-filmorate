package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptoin.LogicalException;
import ru.yandex.practicum.filmorate.exceptoin.NoContentException;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;


    @Override
    public User[] addFriend(Long userId, Long friendId) {
        // пытаемся получить пользователей из хранилища и проходим валидацию на наличие этих пользователей в приложении
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        final User friend = storage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with id=" + friendId));
        // Выполняем основную логику по добавлению в друзья
        // добавление friend в друзья к user
        cooperation(friend, user);
        // добавление user в друзья к friend
        cooperation(user, friend);
        return new User[]{storage.update(user), storage.update(friend)};
    }

    private void cooperation(User user, User friend) {
        if (friend.getFriendsId() == null) {
            HashMap<Long, FriendStatus> newMap = new HashMap<>();
            newMap.put(user.getId(), FriendStatus.WAITING);  // при добавлении в друзья первичный статус - неподтвержденный
            friend.setFriendsId(newMap);
        } else {
            friend.getFriendsId().put(user.getId(), FriendStatus.WAITING);
        }
    }

    @Override
    public User[] removeFriend(Long userId, Long friendId) {
        // пытаемся получить пользователей из хранилища и проходим валидацию на наличие этих пользователей в приложении
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        final User friend = storage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with id=" + friendId));
        // Проверяем есть ли в друзьях пользователи
        if (user.getFriendsId() == null || friend.getFriendsId() == null) {
            throw new NoContentException("Users: " + user.getName() + " or " +
                    friend.getName() + " were not added friends.");
        }
        if (user.getFriendsId().containsKey(friendId) && friend.getFriendsId().containsKey(userId)) {
            // Выполняем основную логику по удалению из друзей и обновляем репозиторий
            user.getFriendsId().remove(friend.getId());
            friend.getFriendsId().remove(user.getId());
            return new User[]{storage.update(user), storage.update(friend)};
        } else {
            throw new NoContentException("Users: " + user.getName() + " and " +
                    friend.getName() + " were not added to friends before.");
        }
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        // пытаемся получить пользователей из хранилища и проходим валидацию на наличие этих пользователей в приложении
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
        if (user.getFriendsId() == null) {
            return Collections.emptyList();
            //throw new NoContentException("User " + user.getName() + " has no friends");
        }
        Set<Long> friendIds = user.getFriendsId().keySet();
        return storage.getFriends(friendIds);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        // пытаемся получить пользователей из хранилища и проходим валидацию на наличие этих пользователей в приложении
        final User user = storage.get(id)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + id));
        final User otherUser = storage.get(otherId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + otherId));
        if (user.getFriendsId() == null) {
            throw new LogicalException("User " + user.getName() + " has no friends");
        }
        if (otherUser.getFriendsId() == null) {
            throw new NotFoundException("User " + otherUser.getName() + " has no friends");
        }
        final Set<Long> userFriendIds = user.getFriendsId().keySet();
        final Set<Long> otherUserFriendIds = otherUser.getFriendsId().keySet();
        // находим пересечение двух множеств друзей
        userFriendIds.retainAll(otherUserFriendIds);
        return storage.getFriends(userFriendIds);
    }

    @Override
    public Collection<User> getAll() {
        return storage.getAll();
    }

    @Override
    public User create(User user) {
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.add(user);
    }

    @Override
    public User update(User newUser) {
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        // если пользователь по id не найден выбрасвываем исключение
        storage.get(newUser.getId())
                .orElseThrow(() -> new NotFoundException("User " + newUser.getName() + " with id="
                        + newUser.getId() + " not found."));
        // выполняем основную логику по обновлению
        return storage.update(newUser);
    }
}
