package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final RequestService requestService;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        ItemDto itemDto = new ItemDto(1, "Дрель", "Ударная дрель", true, 1);
        itemRequestDto = ItemRequestDto
                .builder()
                .id(1)
                .description("Ударная дрель")
                .created(LocalDateTime.now())
                .items(List.of(itemDto, itemDto))
                .build();
    }

    @Test
    @SneakyThrows
    void shouldCreateItemRequest() {
        when(requestService.add(any(), any(Integer.class))).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.items").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetByUserId() {
        when(requestService.getByUserId(any(Integer.class))).thenReturn(List.of(itemRequestDto, itemRequestDto));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Ударная дрель"))
                .andExpect(jsonPath("$[0].items").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetAllRequests() {
        when(requestService.getAll(any(Integer.class), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Ударная дрель"))
                .andExpect(jsonPath("$[0].items").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetRequestByIdT() {
        when(requestService.get(any(Integer.class), any(Integer.class))).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.items").isNotEmpty());
    }
}