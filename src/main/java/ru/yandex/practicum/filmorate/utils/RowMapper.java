package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@UtilityClass
public class RowMapper {

    public static User mapRowToUser(ResultSet rs, int row) throws SQLException {
        return new User(rs.getInt("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
                rs.getDate("BIRTHDAY").toLocalDate());
    }

    public static Integer mapRowToFriendsIds(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("FRIEND_ID");
    }

    public static Film mapRowToFilm(ResultSet rs, int row) throws SQLException {
        return new Film(rs.getInt("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION")
        );
    }

    public static Genre mapRowToGenre(ResultSet rs, int row) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"),
                rs.getString("GENRE_NAME"));
    }

    public static MPA mapRowToMPA(ResultSet rs, int row) throws SQLException {
        return new MPA(rs.getInt("MPA_ID"),
                rs.getString("MPA_NAME")
        );
    }

    public static int mapRowToLikes(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("USER_ID");
    }
}
