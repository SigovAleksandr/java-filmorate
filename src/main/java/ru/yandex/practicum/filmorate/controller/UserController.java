package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    HashMap<Integer, User> users = new HashMap<>();
    int id = 0;

    private int getId() {
        id = id + 1;
        return id;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        return usersList;
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        user.setId(getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("User with id " + user.getId() + " not found");
        }
    }
}
