package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private FilmController controller;
    private InMemoryFilmStorage filmStorage;
    private UserService userService;

    @BeforeEach
    public void setup() {
        filmStorage = new InMemoryFilmStorage();
        userService = new UserService(new InMemoryUserStorage());
        controller = new FilmController(new FilmService(filmStorage, userService));
    }

    @Test
    void createValidFilmTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        controller.addFilm(film);
        assertEquals(film, controller.getAllFilms().get(0));
    }

    @Test
    void updateFilmTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        controller.addFilm(film);
        film.setName("updated");
        controller.updateFilm(film);
        assertEquals(film, controller.getAllFilms().get(0));
    }

    @Test
    void updateFilmWithUnknownIdTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
                () -> controller.updateFilm(film)
        );
        assertEquals("Movie with id 0 not found", thrown.getMessage());
    }

    @Test
    void getAllFilmsTest() {
        Film film1 = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        Film film2 = new Film(0, "test222", "description222",
                LocalDate.parse("2000-12-10"), 120, null);
        controller.addFilm(film1);
        controller.addFilm(film2);
        assertEquals(2, controller.getAllFilms().size());
    }

    @Test
    void filmWithInvalidNameTest() {
        Film film = new Film(0, "", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidDescriptionTest() {
        Film film = new Film(0, "qqq", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 " +
                "миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.parse("2000-12-04"), 120, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidReleaseDateTest() {
        Film film = new Film(0, "", "description",
                LocalDate.parse("1700-12-04"), 120, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidDurationTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), -10, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void addLikeShouldAddLikeToFilmTest() {
        Film film1 = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        controller.addFilm(film1);
        userService.addUser(user1);
        controller.addLike(1, 1);
        assertEquals(controller.getFilmById(1).getLikes().size(), 1);
    }

    @Test
    void deleteLikeShouldDeleteLikeFromFilmTest() {
        Film film1 = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        controller.addFilm(film1);
        userService.addUser(user1);
        controller.addLike(1, 1);
        controller.deleteLike(1, 1);
        assertEquals(controller.getFilmById(1).getLikes().size(), 0);
    }

    @Test
    void getMostPopularFilmsTest() {
        Film film1 = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120, null);
        Film film2 = new Film(0, "test222", "description222",
                LocalDate.parse("2000-12-10"), 120, null);
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        controller.addFilm(film1);
        controller.addFilm(film2);
        userService.addUser(user1);
        controller.addLike(1, 1);
        assertEquals(controller.getMostPopularFilms(1).size(), 1);
        assertEquals(controller.getMostPopularFilms(1).get(0), controller.getFilmById(1));
    }
}
