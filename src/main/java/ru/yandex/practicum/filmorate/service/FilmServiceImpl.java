package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptoin.LogicalException;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    private final Comparator<Film> likeComparator = (o1, o2) -> {
        Optional<Set<Long>> optLikes1 = Optional.ofNullable(o1.getLikesId());
        Optional<Set<Long>> optLikes2 = Optional.ofNullable(o2.getLikesId());
        if (optLikes1.isPresent() && optLikes2.isPresent()) {
            int likes1 = o1.getLikesId().size();
            int likes2 = o2.getLikesId().size();
            if (likes1 > likes2) return -1;
            else return 1;
        }
        return 0;
    };

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film create(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film newFilm) {
        // Если фильм не найден в хранилище - выбрасываем исключение
        filmStorage.get(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Film " + newFilm.getName() +
                        " with id=" + newFilm.getId() + " not found."));
        // выполняем основную логику по обновлению
        return filmStorage.update(newFilm);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        // Если фильм или пользователь не найдены в хранилищах - выбрасываем исключение
        Film film = filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found."));
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        if (film.getLikesId() == null) {
            HashSet<Long> newSet = new HashSet<>();
            newSet.add(userId);
            film.setLikesId(newSet);
        } else {
            // проверяем был ли добавлен лайк фильму от пользователя ранее
            long likesId = film.getLikesId().stream()
                    .filter(like -> Objects.equals(userId, like))
                    .count();
            if (likesId > 0) {
                throw new LogicalException("User: " + user.getName() +
                        " already added a like to the film " + film.getName());
            }
            film.getLikesId().add(userId);
        }
        return filmStorage.update(film);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        // Если фильм или пользователь не найдены в хранилищах - выбрасываем исключение
        Film film = filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found."));
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        if (film.getLikesId() == null) {
            throw new LogicalException("Film " + film.getName() + " has not are lakes for subtract.");
        }
        // проверяем был ли добавлен лайк фильму от пользователя ранее
        long likesId = film.getLikesId().stream()
                .filter(like -> Objects.equals(userId, like))
                .count();
        if (likesId >= 1) {
            film.getLikesId().remove(userId);
            return filmStorage.update(film);
        }
        throw new LogicalException("Film: " + film.getName() + " has not a lake from the User: " +
                user.getName() + " id=" + userId);

    }

    @Override
    public Collection<Film> showPopularFilms(int amount) {
        Collection<Film> films = filmStorage.getAll();
        // отделяем фильмы имеющие лайки
        Collection<Film> filmsWithLike = films.stream()
                .filter(film -> film.getLikesId() != null)
                .toList();
        // сортируем и обрезаем по кол-ву amount
        Collection<Film> sortedFilm = new ArrayList<>(filmsWithLike.stream()
                .sorted(likeComparator)
                .limit(amount)
                .toList());
        // если остаются фильмы без лайков, которые нужно вернуть, то добавляем их в конец списка.
        if (sortedFilm.size() < amount) {
            int residual = amount - sortedFilm.size();
            Collection<Film> filmsWithoutLikes = films.stream()
                    .filter(film -> film.getLikesId() == null)
                    .limit(residual)
                    .toList();
            sortedFilm.addAll(filmsWithoutLikes);
        }
        return sortedFilm;
    }
}
