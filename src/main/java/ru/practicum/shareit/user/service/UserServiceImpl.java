package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final ItemStorage itemStorage;

    @Override
    public UserDto create(UserDto userDto) {
        User u = userStorage.save(UserDto.fromUserDto(userDto));
        itemStorage.addUser(u.getId());
        return UserDto.toUserDto(u);
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        return UserDto.toUserDto(userStorage.update(UserDto.fromUserDto(userDto), id));
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return UserDto.toUserDto(userStorage.getById(userId));
    }

    @Override
    public boolean deleteUserById(Integer userId) {
        return userStorage.delete(userId);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getList().stream()
                .map(user -> UserDto.toUserDto(user))
                .collect(Collectors.toList());
    }
}