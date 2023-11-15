package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    private BookingDto bookingDto;
    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "user", "user@user.com");
        user2 = new User(2, "user2", "user2@user.com");
        item = new Item(1, "tool", "real tool", true, user1.getId(), null);
        bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1, ItemDto.toItemDto(item), UserDto.toUserDto(user2), Status.WAITING);
    }

    @Test
    void shouldThrowIfItemIsNotExist() {
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        bookingDto.setItem(null);
        bookingDto.setItemId(8);
        assertThrows(NotFoundException.class, () -> bookingService.add(bookingDto, 1));
    }

    @Test
    void shouldThrowIfItemIsNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfCreatorIsOwner() {
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(1)).thenReturn(UserDto.toUserDto(user1));
        assertThrows(NotFoundException.class, () -> bookingService.add(bookingDto, 1));
    }

    @Test
    void shouldThrowIfCreateWithEmptyStart() {
        bookingDto.setStart(null);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(2)).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfCreateWithEmptyEnd() {
        bookingDto.setEnd(null);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(2)).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfCreateWithStartAfterEnd() {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(2)).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfCreateWithStartBeforeNow() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(2)).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfCreateWithStartEqualsEnd() {
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(2)).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2));
    }

    @Test
    void shouldThrowIfUpdateNotExistBooking() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(BookingException.class, () -> bookingService.update(3, 2, true));
    }

    @Test
    void shouldThrowIfUpdateNotOwner() {
        bookingDto.setId(1);
        Booking booking = BookingDto.fromBookingDto(bookingDto);
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(booking));
        when(userService.getUserById(any(Integer.class))).thenReturn(UserDto.toUserDto(user1));
        assertThrows(NotFoundException.class, () -> bookingService.update(1, 2, true));
    }

    @Test
    void shouldGetByOwner() {
        bookingDto.setId(1);
        Booking booking = BookingDto.fromBookingDto(bookingDto);
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(any(Integer.class))).thenReturn(UserDto.toUserDto(user1));

        assertDoesNotThrow(() -> bookingService.getBooking(1, 1));
    }

    @Test
    void shouldThrowIfAskNotExistBooking() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(3, 2));
    }

    @Test
    void shouldThrowIfAskBookingWithNotOwnerId() {
        bookingDto.setId(1);
        Booking booking = BookingDto.fromBookingDto(bookingDto);
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(booking));
        when(userService.getUserById(any(Integer.class))).thenReturn(UserDto.toUserDto(user2));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 3));
    }

    @Test
    void shouldThrowIfAskAllBookingsForUserWithUnknownState() {
        when(userService.getUserById(any(Integer.class))).thenReturn(UserDto.toUserDto(user1));
        assertThrows(BookingException.class,
                () -> bookingService.getAllByBooker("Unknown", 1, 2, 1));
    }

    @Test
    void shouldThrowIfAskAllBookingsForOwnerWithUnknownState() {
        when(userService.getUserById(any(Integer.class))).thenReturn(UserDto.toUserDto(user2));
        assertThrows(BookingException.class,
                () -> bookingService.getAllByOwner("Unknown", 2, 2, 1));
    }
}