package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utils.RowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Primary
@Repository
public class FilmDBStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;
    MPAStorage mpaStorage;
    GenreStorage genreStorage;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, MPAStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT F.FILM_ID,\n" +
                "F.FILM_NAME,\n" +
                "F.DESCRIPTION,\n" +
                "F.RELEASE_DATE,\n" +
                "F.DURATION,\n" +
                "F.RATING,\n" +
                "M.MPA_ID,\n" +
                "M.MPA_NAME,\n" +
                "GL.GENRE_ID,\n" +
                "GL.GENRE_NAME\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN MPA_RATING M ON F.RATING = M.MPA_ID\n" +
                "LEFT JOIN FILM_GENRES FG on F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN GENRES_LIST GL on FG.GENRE_ID = GL.GENRE_ID\n";
        return jdbcTemplate.query(query, (ResultSetExtractor<List<Film>>) this::getFilmListFromDB);
    }

    @Override
    public Film getFilmById(int id) {
        String query = "SELECT F.FILM_ID,\n" +
                "F.FILM_NAME,\n" +
                "F.DESCRIPTION,\n" +
                "F.RELEASE_DATE,\n" +
                "F.DURATION,\n" +
                "F.RATING,\n" +
                "M.MPA_ID,\n" +
                "M.MPA_NAME,\n" +
                "GL.GENRE_ID,\n" +
                "GL.GENRE_NAME\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN MPA_RATING M ON F.RATING = M.MPA_ID\n" +
                "LEFT JOIN FILM_GENRES FG on F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN GENRES_LIST GL on FG.GENRE_ID = GL.GENRE_ID\n" +
                "WHERE F.FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(query, RowMapper::mapRowToFullFilm, id);
        if (films.isEmpty()) {
            throw new ResourceNotFoundException("Film not found");
        }
        films.get(0).setLikes(getLikesForFilm(id));
        return films.get(0);
    }

    @Override
    public Film addFilm(Film film) {
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
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {
            String queryTwo = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(queryTwo, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, film.getGenres().get(i).getId());
                    keyHolder.getKey();
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            film.setLikes(new ArrayList<>());
        } else {
            String queryTwo = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(queryTwo, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, film.getLikes().get(i));
                }

                @Override
                public int getBatchSize() {
                    return film.getLikes().size();
                }
            });
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        String query = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING = ? "
                + "WHERE FILM_ID = ?";
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            String queryDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
            jdbcTemplate.update(queryDelete, film.getId());
            film.setGenres(new ArrayList<>());
        } else {
            List<Genre> list = new ArrayList<>();
            for (Genre g : film.getGenres()) {
                if (!list.contains(g)) {
                    list.add(g);
                }
            }
            film.setGenres(list);
            String queryDelete = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
            jdbcTemplate.update(queryDelete, film.getId());
            String queryUpdate = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(queryUpdate, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, film.getGenres().get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            film.setLikes(new ArrayList<>());
        } else {
            String queryTwo = "MERGE INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(queryTwo, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, film.getLikes().get(i));
                }

                @Override
                public int getBatchSize() {
                    return film.getLikes().size();
                }
            });
        }
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

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String query = "SELECT F.FILM_ID,\n" +
                "F.FILM_NAME,\n" +
                "F.DESCRIPTION,\n" +
                "F.RELEASE_DATE,\n" +
                "F.DURATION,\n" +
                "F.RATING,\n" +
                "M.MPA_ID,\n" +
                "M.MPA_NAME,\n" +
                "GL.GENRE_ID,\n" +
                "GL.GENRE_NAME\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN MPA_RATING M ON F.RATING = M.MPA_ID\n" +
                "LEFT JOIN FILM_GENRES FG on F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN GENRES_LIST GL on FG.GENRE_ID = GL.GENRE_ID\n" +
                "LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID\n" +
                "GROUP BY F.FILM_ID, GL.GENRE_ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(query, (ResultSetExtractor<List<Film>>) this::getFilmListFromDB, count);
    }

    private ArrayList<Film> getFilmListFromDB(ResultSet rs) throws SQLException {
        Map<Integer, Film> filmMap = new HashMap<>();
        while (rs.next()) {
            int filmId = rs.getInt("FILM_ID");
            if (filmMap.containsKey(filmId)) {
                Film film = filmMap.get(filmId);
                film.getGenres().add(new Genre(rs.getInt("GENRE_ID"),
                        rs.getString("GENRE_NAME")));
            } else {
                Film film = new Film();
                film.setId(rs.getInt("FILM_ID"));
                film.setName(rs.getString("FILM_NAME"));
                film.setDescription(rs.getString("DESCRIPTION"));
                film.setReleaseDate(rs.getObject("RELEASE_DATE", LocalDate.class));
                film.setDuration(rs.getInt("DURATION"));
                film.setMpa(new MPA(rs.getInt("MPA_ID"),
                        rs.getString("MPA_NAME")));
                List<Genre> genres = new ArrayList<>();
                if (rs.getInt("GENRE_ID") != 0) {
                    genres.add(new Genre(rs.getInt("GENRE_ID"),
                            rs.getString("GENRE_NAME")));
                }
                film.setGenres(genres);
                filmMap.put(filmId, film);
            }
        }
        return new ArrayList<>(filmMap.values());
    }

    private List<Integer> getLikesForFilm(int id) {
        String query = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(query, RowMapper::mapRowToLikes, id);
    }
}
