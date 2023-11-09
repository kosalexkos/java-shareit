package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Integer id);

    UserDto getUserById(Integer userId);

    boolean deleteUserById(Integer userId);

    void deleteAllUsers();

    List<UserDto> getAllUsers();
}