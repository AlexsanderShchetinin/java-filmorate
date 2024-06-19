package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    static final long FILM_ID = 1L;

    private Film getTestFilm() {
        return Film.builder()
                .id(FILM_ID)
                .name("Night")
                .description("usual description")
                .releaseDate(LocalDate.of(2001, 6, 6))
                .duration(120)
                .mpa(new Mpa(3, "PG-13"))
                .build();
    }

    private Film getTestCreatingFilm() {
        return Film.builder()
                .name("Night2")
                .description("usual description create")
                .releaseDate(LocalDate.of(2003, 6, 6))
                .duration(110)
                .mpa(new Mpa(5, "NC-17"))
                .build();
    }

    List<Genre> getTestGenres() {
        return List.of(new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    List<Mpa> getTestMpas() {
        return List.of(new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }


    @Test
    void create() {
        Film createdFilm = filmDbStorage.create(getTestCreatingFilm());
        Optional<Film> filmOptional = filmDbStorage.get(createdFilm.getId());
        Film checkedFilm = getTestCreatingFilm();
        checkedFilm.setId(createdFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(checkedFilm);
    }

    @Test
    void update() {
        Film film = getTestCreatingFilm();
        film.setId(FILM_ID);
        Film updatedFilm = filmDbStorage.update(film);
        assertThat(updatedFilm)
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void getAll() {
        List<Film> filmsDb = filmDbStorage.getAll();
        assertEquals(5, filmsDb.size());
        assertEquals(3, filmsDb.get(2).getGenres().size());
        assertEquals("R", filmsDb.get(3).getMpa().getName());
        assertEquals("unusual", filmsDb.get(1).getDescription());

    }

    @Test
    void getByIds() {
        LinkedList<Long> ids = new LinkedList<>(List.of(1L, 3L, 5L));
        List<Film> films = filmDbStorage.getByIds(ids);
        assertEquals(3, films.size());
        assertEquals(1L, films.getFirst().getId());
        assertEquals(5L, films.getLast().getId());
        assertEquals(100, films.get(1).getDuration());

    }

    @Test
    void get() {
        Optional<Film> filmOptional = filmDbStorage.get(FILM_ID);
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestFilm());
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = filmDbStorage.getAllGenres();
        assertThat(genres)
                .usingRecursiveComparison()
                .isEqualTo(getTestGenres());
    }

    @Test
    void getGenre() {
        Optional<Genre> genre = filmDbStorage.getGenre(2);
        assertThat(genre)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(new Genre(2, "Драма"));
    }


    @Test
    void getAllMpas() {
        List<Mpa> mpas = filmDbStorage.getAllMpas();
        assertThat(mpas)
                .usingRecursiveComparison()
                .isEqualTo(getTestMpas());
    }

    @Test
    void getMpa() {
        Optional<Mpa> mpa = filmDbStorage.getMpa(3);
        assertThat(mpa)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(new Mpa(3, "PG-13"));
    }

    @Test
    void addLike() {
        filmDbStorage.addLike(2, 4);
        filmDbStorage.addLike(2, 3);
        filmDbStorage.addLike(2, 1);
        List<Film> films = (List<Film>) filmDbStorage.showPopularFilms(3);
        assertEquals(2L, films.getFirst().getId());
    }

    @Test
    void removeLike() {
        filmDbStorage.removeLike(3, 5);
        filmDbStorage.removeLike(3, 2);
        List<Film> films = (List<Film>) filmDbStorage.showPopularFilms(3);
        assertEquals(1L, films.getFirst().getId());
    }

    @Test
    void showPopularFilms() {
        List<Film> films = (List<Film>) filmDbStorage.showPopularFilms(3);
        assertEquals(3L, films.getFirst().getId());
    }
}