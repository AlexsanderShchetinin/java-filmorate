package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> repository;
    private long counter;

    public InMemoryUserStorage(Map<Long, User> repository) {
        this.repository = repository;
        this.counter = 0L;
    }


    @Override
    public Collection<User> getAll() {
        return repository.values();
    }


    @Override
    public Collection<User> getFriends(Set<Long> friendIds) {
        // ищем нужных друзей из репозитория по входному множеству id
        return repository.values().stream()
                .filter(friend -> friendIds.contains(friend.getId()))
                .collect(Collectors.toList());
    }


    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public User add(User user) {
        // при создании пользователя устанавливаем для его id
        user.setId(++counter);
        // сохраняем нового пользователя в памяти приложения
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        // меняем пользователя в таблице
        repository.put(newUser.getId(), newUser);
        return newUser;
    }


}
