package ru.practicum.shareit.user.storage;

import java.util.List;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    User save(User user);

    User update(User user, Integer id);

    User getById(Integer id);

    boolean delete(Integer id);

    void deleteAllUsers();

    List<User> getList();
}