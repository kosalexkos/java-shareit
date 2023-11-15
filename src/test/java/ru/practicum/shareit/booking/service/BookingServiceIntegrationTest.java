package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userService.create(new UserDto(null,
                "dude",
                "dude@dude.com"));
        UserDto booker = userService.create(new UserDto(null,
                "dude2",
                "dude2@dude.com"));
        ItemDto item = itemService.create(new ItemDto(null,
                "tool",
                "nice tool",
                true,
                null), 1);
        bookingDto = new BookingDto(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                item, booker, Status.WAITING);
    }

    @Test
    void shouldCreateBooking() {
        BookingDto createdBooking = bookingService.add(bookingDto, 2);
        assertNotNull(bookingService.getBooking(1, 2));
        assertEquals(bookingService.getBooking(1, 2).getStatus(), createdBooking.getStatus());
        assertEquals(bookingService.getBooking(1, 2).getBooker(), createdBooking.getBooker());
        assertEquals(bookingService.getBooking(1, 2).getItem(), createdBooking.getItem());
    }

    @Test
    void shouldUpdateBooking() {
        bookingService.add(bookingDto, 2);
        assertEquals(Status.APPROVED, bookingService.update(1, 1, true).getStatus());
    }

    @Test
    void getBookingById() {
        bookingService.add(bookingDto, 2);
        BookingDto actualBooking = bookingService.getBooking(1, 2);
        assertEquals(bookingDto.getItem(), actualBooking.getItem());
        assertEquals(bookingDto.getBooker(), actualBooking.getBooker());
        assertEquals(bookingDto.getStatus(), actualBooking.getStatus());
        assertEquals(bookingDto.getItemId(), actualBooking.getItemId());
    }

    @Test
    void getBookingsByBooker() {
        bookingService.add(bookingDto, 2);
        List<BookingDtoResponse> bookingDtoList = bookingService.getAllByBooker("ALL", 2, 0, 1);
        assertEquals(1, bookingDtoList.size());

        List<BookingDtoResponse> bookingDtoList2 = bookingService.getAllByBooker("FUTURE", 2, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList2);

        List<BookingDtoResponse> bookingDtoList3 = bookingService.getAllByBooker("REJECTED", 2, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList3);

        List<BookingDtoResponse> bookingDtoList4 = bookingService.getAllByBooker("CURRENT", 2, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList4);

        List<BookingDtoResponse> bookingDtoList5 = bookingService.getAllByBooker("PAST", 2, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList5);

        List<BookingDtoResponse> bookingDtoList6 = bookingService.getAllByBooker("WAITING", 2, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList6);
    }

    @Test
    void getBookingsByOwner() {
        bookingService.add(bookingDto, 2);

        List<BookingDtoResponse> bookingDtoList = bookingService.getAllByOwner("ALL", 1, 0, 1);
        assertEquals(1, bookingDtoList.size());

        List<BookingDtoResponse> bookingDtoList2 = bookingService.getAllByOwner("FUTURE", 1, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList2);

        List<BookingDtoResponse> bookingDtoList3 = bookingService.getAllByOwner("REJECTED", 1, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList3);

        List<BookingDtoResponse> bookingDtoList4 = bookingService.getAllByOwner("CURRENT", 1, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList4);

        List<BookingDtoResponse> bookingDtoList5 = bookingService.getAllByOwner("PAST", 1, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList5);

        List<BookingDtoResponse> bookingDtoList6 = bookingService.getAllByOwner("WAITING", 1, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList6);
    }
}