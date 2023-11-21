package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.List;

import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService service;
    private final String path = "/{id}";

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Request to create a new user");
        return service.create(user);
    }

    @PatchMapping(path)
    public UserDto updateUser(@Valid @PathVariable("id") Integer id, @RequestBody UserDto userDto) {
        log.info("Request to update user");
        return service.update(userDto, id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Request to get list of all users");
        return service.getAllUsers();
    }

    @GetMapping(path)
    public UserDto getUserById(@PathVariable Integer id) {
        log.info("Request to get user with id {}", id);
        return service.getUserById(id);
    }

    @DeleteMapping(path)
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        log.info("Request to delete user with id {}", id);
        service.deleteUserById(id);
        return ResponseEntity.ok().body(String.format("User with id %s was successfully removed", id));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteAll() {
        log.info("Request to delete all users");
        service.deleteAllUsers();
        return ResponseEntity.ok().body("All users were successfully removed");
    }
}