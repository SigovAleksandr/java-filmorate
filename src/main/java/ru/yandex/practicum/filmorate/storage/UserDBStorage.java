package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.RowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Primary
@Repository
public class UserDBStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String query = "SELECT * from USERS";
        List<User> users = jdbcTemplate.query(query, RowMapper::mapRowToUser);
        return users;
    }

    @Override
    public User getUserById(int id) {
        String query = "SELECT * FROM USERS WHERE USER_ID = ?";
        List<User> users = jdbcTemplate.query(query, RowMapper::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Film not found");
        }
        List<Integer> friends = getFriendsList(id);
        users.get(0).setFriends(friends);
        return users.get(0);
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "INSERT INTO USERS (LOGIN, USER_NAME, EMAIL, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        user.setFriends(new ArrayList<>());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String queryOne = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(queryOne, user.getId());
        if (!rows.next()) {
            throw new ResourceNotFoundException("User not found");

        }
        List<Integer> temp = new ArrayList<>();
        if (user.getFriends() == null) {
            user.setFriends(temp);
        } else if (user.getFriends() != null || !user.getFriends().isEmpty()) {
            temp = user.getFriends();
        }
        String queryTwo = "UPDATE USERS SET EMAIL = ?, USER_NAME = ?, LOGIN = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(queryTwo, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday(), user.getId());
        user.setFriends(temp);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String queryOne = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(queryOne, userId);
        if (!rows.next()) {
            throw new ResourceNotFoundException("User not found");
        }
        rows = jdbcTemplate.queryForRowSet(queryOne, friendId);
        if (!rows.next()) {
            throw new ResourceNotFoundException("Friend not found");
        }
        String queryTwo = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, STATUS_CODE) VALUES (?, ?, ?)";
        jdbcTemplate.update(queryTwo, userId, friendId, 1);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String queryOne = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(queryOne, userId);
        if (!rows.next()) {
            throw new ResourceNotFoundException("User not found");
        }
        rows = jdbcTemplate.queryForRowSet(queryOne, friendId);
        if (!rows.next()) {
            throw new ResourceNotFoundException("Friend not found");
        }
        String queryTwo = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryTwo, userId, friendId);
    }

    private List<Integer> getFriendsList(int id) {
        Set friends = new HashSet<>();
        String query = "SELECT FRIEND_ID FROM FRIENDS F\n" +
                "JOIN FRIENDSHIP_STATUS FS on f.STATUS_CODE = FS.STATUS_CODE\n" +
                "WHERE F.USER_ID = ?\n" +
                "AND FS.STATUS_CODE = 1;";
        return jdbcTemplate.query(query, RowMapper::mapRowToFriendsIds, id);
    }
}
