package com.example.application.gateway.user;

import com.example.application.common.container.App;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@App.Bean
public class UserService {

    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(userStore.values());
    }

    public User getUserById(String id) {
        User user = userStore.get(id);
        if (user == null) {
            throw new NoSuchElementException("User not found: " + id);
        }
        return user;
    }

    public User createUser(User user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        userStore.put(id, user);
        return user;
    }

    public User updateUser(String id, User updatedUser) {
        if (!userStore.containsKey(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        updatedUser.setId(id);
        userStore.put(id, updatedUser);
        return updatedUser;
    }

    public void deleteUser(String id) {
        if (userStore.remove(id) == null) {
            throw new NoSuchElementException("User not found for ID: " + id);
        }
    }
}