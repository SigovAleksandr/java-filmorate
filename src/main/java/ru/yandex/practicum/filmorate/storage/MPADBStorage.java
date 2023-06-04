package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utils.RowMapper;

import java.util.List;

@Slf4j
@Repository
public class MPADBStorage implements MPAStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getAllMPA() {
        String query = "SELECT * FROM MPA_RATING";
        return jdbcTemplate.query(query, RowMapper::mapRowToMPA);
    }

    @Override
    public MPA getMPAById(int id) {
        String query = "SELECT * FROM MPA_RATING WHERE MPA_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(query, id);
        if (!mpaRows.next()) {
            throw new ResourceNotFoundException("MotionCompany not found");
        }
        return jdbcTemplate.queryForObject(query, RowMapper::mapRowToMPA, id);
    }
}
