package ru.practicum.shareit.user.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exceprion.EmailValidationException;
import ru.practicum.shareit.user.exceprion.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> storage;
    private Integer userId = 1;

    @Override
    public User save(User user) {
        if (isDuplicateEmail(user.getEmail(), user.getId())) {
            throw new EmailValidationException(String.format("Email %s is already in use", user.getEmail()));
        }
        user.setId(provideId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Integer id) {
        if (isDuplicateEmail(user.getEmail(), id)) {
            throw new EmailValidationException(String.format("Email %s is already in use", user.getEmail()));
        }
        if (user.getName() != null) {
            storage.get(id).setName(user.getName());
        }
        if (user.getEmail() != null) {
            storage.get(id).setEmail(user.getEmail());
        }
        return storage.get(id);
    }

    @Override
    public User getById(Integer id) {
        if (!storage.containsKey(id)) {
            throw new NotFoundException("User not found");
        }
        return storage.get(id);
    }

    @Override
    public boolean delete(Integer id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
            return true;
        } else {
            throw new NotFoundException("User for deleting not found");
        }
    }

    @Override
    public void deleteAllUsers() {
        storage.clear();
    }

    @Override
    public List<User> getList() {
        return new ArrayList<>(storage.values());
    }

    private boolean isDuplicateEmail(String email, Integer id) {
        for (User u : storage.values()) {
            if (u.getEmail().equals(email) && !u.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Integer provideId() {
        return this.userId++;
    }
}