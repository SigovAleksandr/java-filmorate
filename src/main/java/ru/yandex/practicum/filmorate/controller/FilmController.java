package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();
    int id = 0;
    LocalDate movieDay = LocalDate.parse("1895-12-28");

    private int getId() {
        id = id + 1;
        return id;
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        List<Film> filmsList = new ArrayList<>(films.values());
        return filmsList;
    }

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(getId());
        if (film.getReleaseDate().isBefore(movieDay)) {
            throw new ValidationException("Movie release date is before 1895-12-29");
        }
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Movie with id " + film.getId() + " not found");
        }
    }
}
