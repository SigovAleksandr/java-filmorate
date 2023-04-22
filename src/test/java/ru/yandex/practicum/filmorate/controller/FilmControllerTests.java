package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    FilmController controller;

    @BeforeEach
    public void setup() {
        controller = new FilmController();
    }

    @Test
    void createValidFilmTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120);
        controller.addFilm(film);
        assertEquals(film, controller.getAllFilms().get(0));
    }

    @Test
    void updateFilmTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120);
        controller.addFilm(film);
        film.setName("updated");
        controller.updateFilm(film);
        assertEquals(film, controller.getAllFilms().get(0));
    }

    @Test
    void updateFilmWithUnknownIdTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120);
        try {
            controller.updateFilm(film);
        } catch (ValidationException e) {
            assertEquals("Movie with id 0 not found", e.getMessage());
        }
    }

    @Test
    void getAllFilmsTest() {
        Film film1 = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), 120);
        Film film2 = new Film(0, "test222", "description222",
                LocalDate.parse("2000-12-10"), 120);
        controller.addFilm(film1);
        controller.addFilm(film2);
        assertEquals(2, controller.getAllFilms().size());
    }

    @Test
    void filmWithInvalidNameTest() {
        Film film = new Film(0, "", "description",
                LocalDate.parse("2000-12-04"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidDescriptionTest() {
        Film film = new Film(0, "", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 " +
                "миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.parse("2000-12-04"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidReleaseDateTest() {
        Film film = new Film(0, "", "description",
                LocalDate.parse("1700-12-04"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void filmWithInvalidDurationTest() {
        Film film = new Film(0, "test", "description",
                LocalDate.parse("2000-12-04"), -10);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }


}
