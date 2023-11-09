package ru.practicum.shareit.user.exceprion;

public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }

    public EmailValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}