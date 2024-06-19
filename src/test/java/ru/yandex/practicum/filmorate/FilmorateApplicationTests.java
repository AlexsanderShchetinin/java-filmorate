package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;


@JdbcTest
@Import({UserDbStorage.class, FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {


    @Test
    void contextLoads() {
    }


}
