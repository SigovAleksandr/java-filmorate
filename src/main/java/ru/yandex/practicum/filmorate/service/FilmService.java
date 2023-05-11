package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

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
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
    }

    public void deleteLike(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmStorage.getAllFilms();
        return films.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
