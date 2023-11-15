package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository repository;

    @Test
    void shouldCreateUser() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        UserDto createdUser = userService.create(UserDto.toUserDto(user));
        assertEquals(user, UserDto.fromUserDto(createdUser));
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        userService.create(UserDto.toUserDto(user));
        UserDto userDto = new UserDto(1, "dude2", "dude2@dude.com");
        when(repository.existsById(any(Integer.class))).thenReturn(true);
        when(repository.getReferenceById(any(Integer.class))).thenReturn(user);
        UserDto updatedUser = userService.update(userDto, 1);
        assertEquals(userDto, updatedUser);
    }

    @Test
    void shouldNotUpdateNotExistingUser() {
        UserDto userDto = new UserDto(1, "dude1", "dude1@dude.com");
        when(repository.existsById(any(Integer.class))).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.update(userDto, 1));
    }

    @Test
    void shouldNotUpdateIfIdIsNull() {
        UserDto userDto = new UserDto(1, "dude1", "dude1@dude.com");
        assertThrows(NotFoundException.class, () -> userService.update(userDto, null));
    }

    @Test
    void shouldNotUpdateUserWithDuplicatedEmail() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        userService.create(UserDto.toUserDto(user));
        User user2 = new User(2, "dude1", "dude1@dude.com");
        when(repository.save(any())).thenReturn(user2);
        userService.create(UserDto.toUserDto(user2));
        UserDto userDto = new UserDto(2, "Alex", "dude@dude.com");
        when(repository.existsById(any(Integer.class))).thenReturn(true);
        when(repository.findByEmail(any())).thenReturn(user);
        assertThrows(EmailValidationException.class, () -> userService.update(userDto, 2));
    }

    @Test
    void shouldUpdateUserWithItsOwnDuplicatedEmail() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        userService.create(UserDto.toUserDto(user));
        User user2 = new User(2, "dude1", "dude1@dude.com");
        when(repository.save(any())).thenReturn(user2);
        userService.create(UserDto.toUserDto(user2));

        UserDto userDto = new UserDto(1, "dude2", "dude@dude.com");

        when(repository.existsById(any(Integer.class))).thenReturn(true);
        when(repository.findByEmail(any())).thenReturn(user);
        when(repository.getReferenceById(any(Integer.class))).thenReturn(user);
        assertDoesNotThrow(() -> userService.update(userDto, 1));
    }

    @Test
    void shouldNotUpdateIfNameAndEmailIsNull() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        userService.create(UserDto.toUserDto(user));

        UserDto userDto = new UserDto(1, null, null);

        when(repository.existsById(any(Integer.class))).thenReturn(true);
        when(repository.getReferenceById(any(Integer.class))).thenReturn(user);

        UserDto actualUser = userService.update(userDto, 1);
        assertNotEquals(userDto, actualUser);
    }

    @Test
    void shouldGetUserById() {
        User user = new User(1, "dude", "dude@dude.com");
        when(repository.save(any())).thenReturn(user);
        userService.create(UserDto.toUserDto(user));

        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        assertEquals(user, UserDto.fromUserDto(userService.getUserById(1)));
    }

    @Test
    void shouldNotGetNonExistentUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = new User(1, "dude", "dude@dude.com");
        User user2 = new User(2, "dude1", "dude1@dude.com");
        when(repository.findAll()).thenReturn(List.of(user1, user2));
        assertEquals(userService.getAllUsers(), List.of(UserDto.toUserDto(user1), UserDto.toUserDto(user2)));
    }

    @Test
    void shouldDeleteUserById() {
        Integer id = 1;
        userService.deleteUserById(id);
        verify(repository, Mockito.times(1)).deleteById(id);
    }
}