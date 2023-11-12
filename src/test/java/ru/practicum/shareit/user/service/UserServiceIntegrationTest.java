package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "dude", "dude@dude.com");
    }

    @Test
    void shouldCreate() {
        UserDto user = userService.create(userDto);
        assertNotNull(user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    void shouldUpdate() {
        userService.create(userDto);
        UserDto updateUser = new UserDto(null, "updatedDude", "updatedDude@dude.com");
        Integer userId = 1;
        UserDto actualUser = userService.update(updateUser, userId);
        assertEquals(updateUser.getName(), actualUser.getName());
        assertEquals(updateUser.getEmail(), actualUser.getEmail());
        assertEquals(userId, actualUser.getId());
    }

    @Test
    void shouldGetAll() {
        UserDto userDto2 = new UserDto(null, "dude2", "dude2@dude.com");
        UserDto user = userService.create(userDto);
        UserDto user2 = userService.create(userDto2);
        List<UserDto> expectedUsers = List.of(user, user2);
        List<UserDto> actualUsers = userService.getAllUsers();
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void shouldGetById() {
        UserDto user = userService.create(userDto);
        assertNotNull(user.getId());
        UserDto actualUser = userService.getUserById(user.getId());
        assertEquals(user, actualUser);
    }

    @Test
    void shouldDeleteById() {
        UserDto user = userService.create(userDto);
        userService.deleteUserById(user.getId());
        assertEquals(new ArrayList<>(), userService.getAllUsers());
    }
}