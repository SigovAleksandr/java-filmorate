package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static Film mapRowToFullFilm(ResultSet rs, int row) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getObject("RELEASE_DATE", LocalDate.class));
        film.setDuration(rs.getInt("DURATION"));
        film.setMpa(new MPA(rs.getInt("MPA_ID"),
                rs.getString("MPA_NAME")));
        film.setGenres(mapGenres(rs));
        return film;
    }

    public static List<Genre> mapGenres(ResultSet rs) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        do {
            if (rs.getInt("GENRE_ID") != 0) {
                genres.add(new Genre(rs.getInt("GENRE_ID"),
                        rs.getString("GENRE_NAME")));
            }
        } while (rs.next());
        return genres;
    }

    private List<Integer> mapLikes(ResultSet rs) throws SQLException {
        List<Integer> likes = new ArrayList<>();
        do {
            likes.add(rs.getInt("USER_ID"));
        } while (rs.next());
        return likes;
    }


}
