package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exceptoin.DbException;
import ru.yandex.practicum.filmorate.exceptoin.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcOperations jdbc;

    private MapSqlParameterSource getUserParams(User user) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValues(Map.of("login", user.getLogin(),
                "username", user.getName(),
                "email", user.getEmail(),
                "birthday", user.getBirthday()));
        return map;
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = getUserParams(user);
        String sqlCreate = "INSERT INTO users(login, username, email, birthday) " +
                "VALUES (:login, :username, :email, :birthday)";
        jdbc.update(sqlCreate, params, keyHolder, new String[]{"user_id"});
        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    @Override
    public User update(User newUser) {

        // Проверяем что запись есть в таблице users
        if (get(newUser.getId()).isEmpty()) {
            throw new NotFoundException("user with id=" + newUser.getId() + " not exist in database");
        }

        String sqlUpdate = "MERGE INTO users(user_id, login, username, email, birthday)" +
                " VALUES (:user_id, :login, :username, :email, :birthday)";
        MapSqlParameterSource params = getUserParams(newUser);
        params.addValues(Map.of("user_id", newUser.getId()));

        if (jdbc.update(sqlUpdate, params) != 1) {
            throw new DbException("Не удалось обновить данные в БД: таблица users  для user.id=" + newUser.getId());
        }
        return newUser;
    }

    @Override
    public List<User> getAll() {
        return jdbc.query("SELECT * FROM users", new UserRowMapper());
    }

    @Override
    public Optional<User> get(long id) {
        String select = "SELECT * FROM users WHERE user_id = :user_id";
        final List<User> users = jdbc.query(select, Map.of("user_id", id), new UserRowMapper());
        return users.stream().findFirst();
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sqlGetFriendId = "SELECT u.* FROM users AS u " +
                "WHERE user_id IN (SELECT friend_id FROM friends AS f WHERE f.user_id=:userId)";
        return jdbc.query(sqlGetFriendId, Map.of("userId", userId), new UserRowMapper());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        // делаем одностороннюю дружбу (только одна запись в таблице)
        String sqlInsertFriend = "MERGE INTO friends(user_id, friend_id) VALUES (:userId, :friendId)";
        jdbc.update(sqlInsertFriend, Map.of("friendId", friendId, "userId", userId));
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sqlRemoveFriend = "DELETE FROM friends WHERE friend_id=:friendId AND user_id=:userId";
        jdbc.update(sqlRemoveFriend, Map.of("friendId", friendId, "userId", userId));
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        String sqlGetCommonFriends = "SELECT u.* " +
                "FROM users AS u " +
                "WHERE user_id IN (SELECT mem.friend_id " +
                "    FROM (SELECT f.friend_id, COUNT(f.friend_id) AS common" +
                "             FROM friends AS f " +
                "             WHERE f.user_id IN (:userId , :otherUserId) " +
                "             GROUP BY f.friend_id) AS mem " +
                "    WHERE common > 1)";
        return jdbc.query(sqlGetCommonFriends,
                Map.of("userId", userId, "otherUserId", otherUserId), new UserRowMapper());
    }
}
