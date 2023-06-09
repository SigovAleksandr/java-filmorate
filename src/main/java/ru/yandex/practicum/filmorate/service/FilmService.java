package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LocalDate movieDay = LocalDate.parse("1895-12-28");

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(movieDay)) {
            throw new ValidationException("Movie release date is before 1895-12-29");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
