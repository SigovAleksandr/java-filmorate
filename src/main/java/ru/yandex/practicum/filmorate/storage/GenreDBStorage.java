package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utils.RowMapper;

import java.util.List;

@Slf4j
@Repository
public class GenreDBStorage implements GenreStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM GENRES_LIST";
        return jdbcTemplate.query(query, RowMapper::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(int id) {
        String query = "SELECT * FROM GENRES_LIST WHERE GENRE_ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(query, id);
        if (!rows.next()) {
            throw new ResourceNotFoundException("Genre not found");
        }
        return jdbcTemplate.queryForObject(query, RowMapper::mapRowToGenre, id);
    }
}
