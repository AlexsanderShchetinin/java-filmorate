package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userServiceJdbcImpl;


    @GetMapping
    public Collection<User> getAll() {
        log.info("==> GET /users <==");
        return userServiceJdbcImpl.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public User create(@RequestBody @Valid User user) {
        log.info("Create User: {} - STARTED", user.getLogin());
        User createdUser = userServiceJdbcImpl.create(user);
        log.info("User {} with id={} - CREATED", user.getLogin(), user.getId());
        return createdUser;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public User update(@RequestBody @Valid User newUser) {
        log.info("Update User: {} - STARTED", newUser.getLogin());
        User updatedUser = userServiceJdbcImpl.update(newUser);
        log.info("User {}  with id={} UPDATED.", newUser.getLogin(), newUser.getId());
        return updatedUser;
    }

    /**
     * @param id       - идентификатор клиента, отправляющего запрос на сервер (т.е. тот кто отправляет приглашение)
     * @param friendId - идентификатор друга, к которому добавляются в друзья
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Friend with id={} try add as friends to User with id={}", friendId, id);
        userServiceJdbcImpl.addFriend(id, friendId);
        log.info("Friend with id={} successfully added as friends to User with id={}", friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Friend with id={} try delete as friends to User with id={}", friendId, id);
        userServiceJdbcImpl.removeFriend(id, friendId);
        log.info("Friend with id={} successfully deleted as friends to User with id={}", friendId, id);
    }

    // возвращаем список пользователей, являющихся друзьями для user.
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("==> GET /friends for user with id={} <==", id);
        return userServiceJdbcImpl.getFriends(id);
    }

    // возвращаем список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("==> GET /common friends for users with id={} and {} <==", id, otherId);
        return userServiceJdbcImpl.getCommonFriends(id, otherId);
    }


}
