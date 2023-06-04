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
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utils.RowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Primary
@Repository
public class FilmDBStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;
    MPAStorage mpaStorage;
    GenreStorage genreStorage;
    private final LocalDate movieDay = LocalDate.parse("1895-12-28");

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, MPAStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT * from FILMS";
        List<Film> films = jdbcTemplate.query(query, RowMapper::mapRowToFilm);
        for (Film film : films) {
            film.setMpa(getMPAForFilm(film.getId()));
            film.setGenres(getGenresForFilm(film.getId()));
            film.setLikes(getLikesForFilm(film.getId()));
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String queryOne = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(queryOne, id);
        if (!rows.next()) {
            throw new ResourceNotFoundException("Film not found");
        }
        String queryTwo = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(queryTwo, RowMapper::mapRowToFilm, id);
        film.setMpa(getMPAForFilm(id));
        film.setGenres(getGenresForFilm(id));
        film.setLikes(getLikesForFilm(id));
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(movieDay)) {
            throw new ValidationException("Movie release date is before 1895-12-29");
        }
        String queryOne = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(queryOne, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(mpaStorage.getMPAById(film.getMpa().getId()));
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {
            String queryTwo = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(queryTwo, film.getId(), genre.getId());
            }
        }
        film.setGenres(getGenresForFilm(film.getId()));
        film.setLikes(new ArrayList<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String queryOne = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(queryOne, film.getId());
        if (!rows.next()) {
            throw new ResourceNotFoundException("Film not found");
        }
        List<Integer> temp = new ArrayList<>();
        if (film.getLikes() == null) {
            film.setLikes(temp);
        } else if (film.getLikes() != null || !film.getLikes().isEmpty()) {
            temp = film.getLikes();
        }
        String queryTwo = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING = ? "
                + "WHERE FILM_ID = ?";
        jdbcTemplate.update(queryTwo, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            String queryDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
            String queryUpdate = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(queryDelete, film.getId());
            for (Genre g : film.getGenres()) {
                String findDuplicate = "select * from FILM_GENRES where FILM_ID = ? AND GENRE_ID = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(findDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(queryUpdate, film.getId(), g.getId());
                }
            }
        }
        film.setMpa(mpaStorage.getMPAById(film.getMpa().getId()));
        film.setGenres(getGenresForFilm(film.getId()));
        film.setLikes(temp);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String query = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String query = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(query, filmId, userId);
    }


    private MPA getMPAForFilm(int id) {
        String query = "SELECT * FROM MPA_RATING M\n" +
                "JOIN FILMS F ON M.MPA_ID = F.RATING\n" +
                "WHERE F.FILM_ID = ?";
        return jdbcTemplate.queryForObject(query, RowMapper::mapRowToMPA, id);
    }

    private List<Genre> getGenresForFilm(int id) {
        String query = "SELECT * FROM GENRES_LIST G\n" +
                "JOIN FILM_GENRES F ON G.GENRE_ID = F.GENRE_ID\n" +
                "WHERE F.FILM_ID = ?";
        return jdbcTemplate.query(query, RowMapper::mapRowToGenre, id);
    }

    private List<Integer> getLikesForFilm(int id) {
        String query = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(query, RowMapper::mapRowToLikes, id);
    }
}
