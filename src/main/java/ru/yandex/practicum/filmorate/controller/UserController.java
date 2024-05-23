package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("==> GET /users <==");
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public User create(@RequestBody @Valid User user) {
        log.info("Create User: {} - STARTED", user.getLogin());
        User createdUser = service.create(user);
        log.info("User {} with id={} - CREATED", user.getLogin(), user.getId());
        return createdUser;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public User update(@RequestBody @Valid User newUser) {
        log.info("Update User: {} - STARTED", newUser.getLogin());
        User updatedUser = service.update(newUser);
        log.info("User {}  with id={} UPDATED.", newUser.getLogin(), newUser.getId());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Friend with id={} try add as friends to User with id={}", friendId, id);
        User[] users = service.addFriend(id, friendId);
        log.info("Friend {} with id={} successfully added as friends to User {} with id={}",
                users[0].getName(), friendId, users[1].getName(), id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Friend with id={} try delete as friends to User with id={}", friendId, id);
        User[] users = service.removeFriend(id, friendId);
        log.info("Friend {} with id={} successfully deleted as friends to User {} with id={}",
                users[0].getName(), friendId, users[1].getName(), id);
    }

    // возвращаем список пользователей, являющихся друзьями для user.
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("==> GET /friends for user with id={} <==", id);
        return service.getFriends(id);
    }

    // возвращаем список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("==> GET /common friends for users with id={} and {} <==", id, otherId);
        return service.getCommonFriends(id, otherId);
    }


}
