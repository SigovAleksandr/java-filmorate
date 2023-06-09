package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public ArrayList<User> getFriendsList(int id) {
        User user = userStorage.getUserById(id);
        ArrayList<User> friendsList = new ArrayList<>();
        for (int i : user.getFriends()) {
            friendsList.add(userStorage.getUserById(i));
        }
        return friendsList;
    }

    public ArrayList<User> getCommonFriendsList(int id, int otherId) {
        User user = userStorage.getUserById(id);
        User otherUser = userStorage.getUserById(otherId);
        Set<Integer> intersection = new HashSet<>(user.getFriends());
        intersection.retainAll(otherUser.getFriends());
        ArrayList<User> commonFriendsList = new ArrayList<>();
        for (int i : intersection) {
            commonFriendsList.add(userStorage.getUserById(i));
        }
        return commonFriendsList;
    }

}
