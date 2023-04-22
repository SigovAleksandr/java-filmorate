package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@RestController
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public HashMap<Integer, Film> getAllFilms() {
        return films;
    }

    @PostMapping("/film")
    public void addFilm(@RequestBody Film film) {
        films.put(film.getId(), film);
    }

    @PutMapping("/film")
    public void updateFilm(@RequestBody Film film) {
        films.put(film.getId(), film);
    }
}
