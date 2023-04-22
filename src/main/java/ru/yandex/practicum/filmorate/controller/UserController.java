package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;

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
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping()
    public void addUser(@Valid @RequestBody User user) {
        users.put(user.getId(), user);
    }

    @PutMapping()
    public void updateUser(@Valid @RequestBody User user) {
        users.put(user.getId(), user);
    }
}
