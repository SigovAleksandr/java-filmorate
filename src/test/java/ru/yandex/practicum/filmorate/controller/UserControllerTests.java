package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UserController controller;

    @BeforeEach
    public void setup() {
        controller = new UserController();
    }

    @Test
    void createValidUserTest() {
        User user = new User(0,"user1@yandex.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"));
        controller.addUser(user);
        assertEquals(user, controller.getAllUsers().get(0));
    }

    @Test
    void updateUserTest() {
        User user = new User(0,"user1@yandex.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"));
        controller.addUser(user);
        user.setName("updated");
        controller.updateUser(user);
        assertEquals(user, controller.getAllUsers().get(0));
    }

    @Test
    void updateUserWithUnknownIdTest() {
        User user = new User(0,"mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"));
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> controller.updateUser(user)
        );
        assertEquals("User with id 0 not found", thrown.getMessage());
    }

    @Test
    void getAllUsersTest() {
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"));
        User user2 = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"));
        controller.addUser(user1);
        controller.addUser(user2);
        assertEquals(2, controller.getAllUsers().size());
    }

    @Test
    void userWithInvalidEmailTest() {
        User user = new User(0,"mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void userWithInvalidBirthdayTest() {
        User user = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("2030-12-12"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void createUserWithoutNameTest() {
        User user = new User(0,"aaa@mail.ru", "login",
                "", LocalDate.parse("2030-12-12"));
        controller.addUser(user);
        assertEquals("login", controller.getAllUsers().get(0).getName());
    }
}
