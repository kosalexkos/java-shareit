package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private final UserService userService;
    private UserDto request;
    private final ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new UserDto(null, "dude", "someDude@dude.com");
    }

    @Test
    @SneakyThrows
    void shouldCreate() {
        when(userService.create(any())).thenReturn(new UserDto(1, request.getName(), request.getEmail()));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("someDude@dude.com"))
                .andExpect(jsonPath("$.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldUpdate() {
        when(userService.update(any(), any(Integer.class)))
                .thenReturn(new UserDto(1, "updatedDude", "somedude@dude.com"));

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("somedude@dude.com"))
                .andExpect(jsonPath("$.name").value("updatedDude"));
    }

    @Test
    @SneakyThrows
    void shouldGetUserById() {
        when(userService.getUserById(any()))
                .thenReturn(new UserDto(1, "dude", "someDude@dude.com"));
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("someDude@dude.com"))
                .andExpect(jsonPath("$.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldGetAllUsers() {
        UserDto user1 = new UserDto(1, request.getName(), request.getEmail());
        UserDto user2 = new UserDto(2, "someUser", "someUser@user.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("someDude@dude.com"))
                .andExpect(jsonPath("$[0].name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldDeleteUserById() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void handlerNotFoundException() {
        when(userService.create(any())).thenThrow(new NotFoundException("something went wrong"));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void handlerEmailValidationException() {
        when(userService.create(any())).thenThrow(new EmailValidationException("Email is already in use"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(409))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailValidationException))
                .andExpect(result -> assertEquals("Email is already in use",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }
}
