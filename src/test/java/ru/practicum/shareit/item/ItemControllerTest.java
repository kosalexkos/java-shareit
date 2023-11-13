package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @MockBean
    private final ItemService itemService;
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private ItemDto itemDto;
    private ItemDtoWithBooking dtoWithBooking;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1, "tool", "some tool", true, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingDtoForItem lastBooking = BookingDtoForItem
                .builder()
                .id(1)
                .bookerId(1)
                .bookingStatus(Status.WAITING)
                .start(start)
                .end(end)
                .build();
        BookingDtoForItem nextBooking = BookingDtoForItem
                .builder()
                .id(1)
                .bookerId(1)
                .bookingStatus(Status.WAITING)
                .start(start.plusDays(2))
                .end(end.plusDays(2))
                .build();
        dtoWithBooking = ItemDtoWithBooking.builder()
                .id(1)
                .name("tool")
                .description("some tool")
                .ownerId(1)
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    @Test
    @SneakyThrows
    void shouldCreateItem() {
        when(itemService.create(any(), any(Integer.class))).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tool"))
                .andExpect(jsonPath("$.description").value("some tool"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @SneakyThrows
    void shouldUpdateItem() {
        itemDto.setAvailable(false);
        when(itemService.update(any(), any(Integer.class), any(Integer.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tool"))
                .andExpect(jsonPath("$.description").value("some tool"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @SneakyThrows
    void shouldGetItemById() {
        when(itemService.getItemById(any(Integer.class), any(Integer.class))).thenReturn(dtoWithBooking);
        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tool"))
                .andExpect(jsonPath("$.description").value("some tool"))
                .andExpect(jsonPath("ownerId").value(1))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.lastBooking").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetItemsByOwner() {
        when(itemService.getItemsByUser(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(dtoWithBooking, dtoWithBooking));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("tool"))
                .andExpect(jsonPath("$[0].description").value("some tool"))
                .andExpect(jsonPath("[0]ownerId").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetItemsByText() {
        when(itemService.getItemsByText(anyString(), any(Integer.class),any(Integer.class)))
                .thenReturn(List.of(itemDto, itemDto));
        mvc.perform(get("/items/search?text=SOME")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("tool"))
                .andExpect(jsonPath("$[0].description").value("some tool"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @SneakyThrows
    void shouldAddCommentTest() {
        Item item = new Item(1, "tool", "some tool", true,
                1, null);
        Comment comment = Comment
                .builder()
                .id(1)
                .authorName("dude")
                .item(item)
                .text("some text")
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(any(Integer.class), any(Integer.class), any())).thenReturn(comment);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.authorName").value("dude"));
    }
}