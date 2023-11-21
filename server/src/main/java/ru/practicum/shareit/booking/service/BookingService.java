package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingDto bookingDto, Integer userId);

    BookingDto update(Integer bookingId, Integer userId, boolean isApproved);

    BookingDto getBooking(Integer bookingId, Integer userId);

    List<BookingDtoResponse> getAllByBooker(String state, Integer userId, Integer from, Integer size);

    List<BookingDtoResponse> getAllByOwner(String state, Integer userId, Integer from, Integer size);
}