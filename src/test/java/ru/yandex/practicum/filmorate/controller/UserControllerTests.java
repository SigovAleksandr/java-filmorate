package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

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
    private InMemoryUserStorage userStorage;

    @BeforeEach
    public void setup() {
        userStorage = new InMemoryUserStorage();
        controller = new UserController(new UserService(userStorage));
    }

    @Test
    void createValidUserTest() {
        User user = new User(0,"user1@yandex.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user);
        assertEquals(user, controller.getAllUsers().get(0));
    }

    @Test
    void updateUserTest() {
        User user = new User(0,"user1@yandex.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user);
        user.setName("updated");
        controller.updateUser(user);
        assertEquals(user, controller.getAllUsers().get(0));
    }

    @Test
    void updateUserWithUnknownIdTest() {
        User user = new User(0,"mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
                () -> controller.updateUser(user)
        );
        assertEquals("User with id 0 not found", thrown.getMessage());
    }

    @Test
    void getAllUsersTest() {
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        User user2 = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user1);
        controller.addUser(user2);
        assertEquals(2, controller.getAllUsers().size());
    }

    @Test
    void userWithInvalidEmailTest() {
        User user = new User(0,"mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void userWithInvalidBirthdayTest() {
        User user = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("2030-12-12"), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void createUserWithoutNameTest() {
        User user = new User(0,"aaa@mail.ru", "login",
                "", LocalDate.parse("2030-12-12"), null);
        controller.addUser(user);
        assertEquals("login", controller.getAllUsers().get(0).getName());
    }

    @Test
    void addFriendShouldAddFriendToBothUsersTest() {
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        User user2 = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(1, 2);
        assertEquals(controller.getUserById(1).getFriends().size(), 1);
        assertEquals(controller.getUserById(2).getFriends().size(), 1);
    }

    @Test
    void deleteFriendShouldDeleteFriendFromBothUsersTest() {
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        User user2 = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(1, 2);
        controller.deleteFriend(1, 2);
        assertEquals(controller.getUserById(1).getFriends().size(), 0);
        assertEquals(controller.getUserById(2).getFriends().size(), 0);
    }

    @Test
    void getCommonFriendsShouldReturnCorrectSizeTest() {
        User user1 = new User(0,"fff@mail.ru", "login22",
                "name of userrrr", LocalDate.parse("1992-12-12"), null);
        User user2 = new User(0,"aaa@mail.ru", "login",
                "name of user", LocalDate.parse("1992-12-12"), null);
        User user3 = new User(0,"aaa@mail.ru", "logi132412n",
                "name ofasdfas user", LocalDate.parse("1992-12-12"), null);
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addUser(user3);
        controller.addFriend(1, 2);
        controller.addFriend(2, 3);
        assertEquals(1, controller.getCommonFriendsList(1, 3).size());
    }
}
