package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    static final long USER_ID = 1L;

    static User getTestUser() {
        return User.builder()
                .id(USER_ID)
                .login("alex")
                .email("email@email.com")
                .build();
    }

    static User getTestCreatingUser() {
        return User.builder()
                .login("testUser")
                .name("45623478")
                .email("Test@email.com")
                .birthday(LocalDate.of(1995, 12, 15))
                .build();
    }


    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userDbStorage.get(USER_ID);
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestUser());
    }

    @Test
    void create() {
        User user = userDbStorage.create(getTestCreatingUser());
        Optional<User> userOptional = userDbStorage.get(user.getId());
        User checkedUser = getTestCreatingUser();
        checkedUser.setId(user.getId());
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(checkedUser);
    }

    @Test
    void update() {
        User user = getTestCreatingUser();
        user.setId(USER_ID);
        User updatedFilm = userDbStorage.update(user);
        Optional<User> filmOptional = userDbStorage.get(updatedFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);
    }

    @Test
    void getAll() {
        List<User> usersDb = userDbStorage.getAll();
        assertEquals(5, usersDb.size());
        assertEquals("emailLen@email.com", usersDb.get(2).getEmail());
        assertEquals(LocalDate.of(1966, 3, 5), usersDb.get(3).getBirthday());
        assertEquals("max33", usersDb.get(1).getName());
    }


    @Test
    void getFriends() {
        Collection<User> friends = userDbStorage.getFriends(1L);
        assertEquals(3L, friends.size());
    }

    @Test
    void addFriend() {
        User createdUser = userDbStorage.create(getTestCreatingUser());
        userDbStorage.addFriend(createdUser.getId(), 1L);
        assertEquals(1, userDbStorage.getFriends(createdUser.getId()).size());
    }

    @Test
    void removeFriend() {
        userDbStorage.removeFriend(1L, 4L);
        assertEquals(2, userDbStorage.getFriends(1).size());
    }

    @Test
    void getCommonFriends() {
        List<User> commonFriend = (List<User>) userDbStorage.getCommonFriends(1L, 3L);
        List<User> users = List.of(userDbStorage.get(4L).get(), userDbStorage.get(5L).get());
        assertThat(commonFriend)
                .usingRecursiveComparison()
                .isEqualTo(users);
    }
}