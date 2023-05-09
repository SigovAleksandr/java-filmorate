package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;
    private final LocalDate movieDay = LocalDate.parse("1895-12-28");

    private int getId() {
        id = id + 1;
        return id;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new ResourceNotFoundException("Film with id " + id + " not found");
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(movieDay)) {
            throw new ValidationException("Movie release date is before 1895-12-29");
        }
        film.setId(getId());
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            Set<Integer> temp = films.get(film.getId()).getLikes();
            film.setLikes(temp);
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ResourceNotFoundException("Movie with id " + film.getId() + " not found");
        }
    }
}
