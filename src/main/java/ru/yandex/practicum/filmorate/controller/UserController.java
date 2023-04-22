package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@RestController
public class UserController {

    HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping("/user")
    public void addUser(@RequestBody User user) {
        users.put(user.getId(), user);
    }

    @PutMapping("/user")
    public void updateUser(@RequestBody User user) {
        users.put(user.getId(), user);
    }
}
