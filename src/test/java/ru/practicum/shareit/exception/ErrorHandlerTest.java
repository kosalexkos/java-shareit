package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {
    @InjectMocks
    private ErrorHandler handler;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private BookingServiceImpl bookingService;

    @Test
    void handleBadRequest() {
        when(bookingService.add(any(), any(Integer.class)))
                .thenThrow(new BookingException("item is not available"));
        BookingException e = assertThrows(BookingException.class,
                () -> bookingService.add(any(), any(Integer.class)));
        assertEquals("item is not available",
                handler.handleBadRequestException(e).get("error"));
    }

    @Test
    void handleNotFound() {
        when(userService.update(any(), any(Integer.class)))
                .thenThrow(new NotFoundException("user not found"));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.update(any(), any(Integer.class)));
        assertEquals("user not found",
                handler.handleNotFoundException(e).get("error"));
    }

    @Test
    void handleConflict() {
        when(userService.update(any(), any(Integer.class)))
                .thenThrow(new EmailValidationException("Email is already in use"));
        EmailValidationException e = assertThrows(EmailValidationException.class,
                () -> userService.update(any(), any(Integer.class)));
        assertEquals("Email is already in use", handler.handleConflictException(e).get("error"));
    }
}
