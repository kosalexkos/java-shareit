package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({EmailValidationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final EmailValidationException e) {
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({BookingException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final BookingException e) {
        return Map.of(
                "error", e.getMessage()
        );
    }
}