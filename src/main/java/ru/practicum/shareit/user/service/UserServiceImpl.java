package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userStorage;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (isDuplicateEmail(userDto.getEmail(), userDto.getId())) {
            userStorage.save(UserDto.fromUserDto(userDto));
            throw new EmailValidationException(String.format("Email %s is already in use", userDto.getEmail()));
        }
        User u = userStorage.save(UserDto.fromUserDto(userDto));
        return UserDto.toUserDto(u);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Integer id) {
        if (id == null || !userStorage.existsById(id)) {
            throw new NotFoundException("Cannot update a non-existent user");
        }
        if (isDuplicateEmail(userDto.getEmail(), id)) {
            throw new EmailValidationException(String.format("Email %s is already in use", userDto.getEmail()));
        }
        User user = userStorage.getReferenceById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return UserDto.toUserDto(userStorage.save(user));
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return UserDto.toUserDto(userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId))));
    }

    @Override
    @Transactional
    public void deleteUserById(Integer userId) {
        userStorage.deleteById(userId);
    }

    @Override
    @Transactional
    public void deleteAllUsers() {
        userStorage.deleteAll();
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserDto::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean isDuplicateEmail(String email, Integer id) {
        User u = userStorage.findByEmail(email);
        if (u != null && !u.getId().equals(id)) {
            return true;
        }
        return false;
    }
}