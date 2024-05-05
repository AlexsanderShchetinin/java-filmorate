package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> userRepository = new HashMap<>();
    private long counter = 0L;

    private long getNextId() {
        counter++;
        return counter;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("==> GET /users <==");
        return userRepository.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.Create.class)
    public User create(@RequestBody @Valid User user) {
        // при создании пользователя устанавливаем для его id
        user.setId(getNextId());
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // сохраняем нового пользователя в памяти приложения
        userRepository.put(user.getId(), user);
        log.info("Создан пользователь {}  с id={}", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    @Validated(Marker.Update.class)
    public User update(@RequestBody @Valid User newUser) throws RuntimeException {
        if (userRepository.containsKey(newUser.getId())) {
            // имя для отображения может быть пустым — в таком случае будет использован логин
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }
            // меняем пользователя в таблице
            userRepository.put(newUser.getId(), newUser);
            log.info("Пользователь {}  с id={} обновлен.", newUser.getLogin(), newUser.getId());
            return newUser;
        }
        // если пользователь по id не найден выбрасвываем исключение
        log.warn("Пользователь с id ={} не найден", newUser.getId());
        throw new RuntimeException("Пользователь с id = " + newUser.getId() + " не найден");
    }

}
