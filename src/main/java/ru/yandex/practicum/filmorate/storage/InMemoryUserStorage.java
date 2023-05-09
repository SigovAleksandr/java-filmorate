package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int getId() {
        id = id + 1;
        return id;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(getId());
        user.setFriends(new HashSet<>());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            Set<Integer> temp = users.get(user.getId()).getFriends();
            user.setFriends(temp);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ResourceNotFoundException("User with id " + user.getId() + " not found");
        }
    }
}
