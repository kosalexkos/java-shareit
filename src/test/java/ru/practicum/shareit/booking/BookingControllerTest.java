package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final BookingService bookingService;
    private User booker;
    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1, "dude", "dude@dude.com");
        User owner = new User(2, "dude2", "dude2@dude.com");
        Item item = new Item(1, "tool", "nice tool", true, owner.getId(), null);
        bookingDto = new BookingDto(1, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1),
                1, ItemDto.toItemDto(item), UserDto.toUserDto(booker), Status.WAITING);
    }

    @Test
    @SneakyThrows
    void shouldCreateBooking() {
        when(bookingService.add(any(), any(Integer.class))).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("tool"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldUpdateBooking() {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.update(any(Integer.class), any(Integer.class), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("tool"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldGetBookingById() {
        when(bookingService.getBooking(any(Integer.class), any(Integer.class))).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("tool"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldGetBookingsByUser() {
        BookingDtoResponse dto = BookingDtoResponse.toBookingDtoResponse(BookingDto.fromBookingDto(bookingDto));
        when(bookingService.getAllByBooker(anyString(), any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(dto, dto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("tool"))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("dude"));
    }

    @Test
    @SneakyThrows
    void shouldGetBookingsByOwner() {
        BookingDtoResponse dto = BookingDtoResponse.toBookingDtoResponse(BookingDto.fromBookingDto(bookingDto));
        when(bookingService.getAllByOwner(anyString(), any(Integer.class),
                any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(dto, dto));

        mvc.perform(get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("tool"))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("dude"));
    }
}
