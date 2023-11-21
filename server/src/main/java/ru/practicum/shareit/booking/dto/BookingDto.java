package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    Integer itemId;
    ItemDto item;
    UserDto booker;
    Status status;

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                ItemDto.toItemDto(booking.getItem()),
                UserDto.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static Booking fromBookingDto(BookingDto dto) {
        return new Booking(dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                ItemDto.fromItemDto(dto.getItem(), dto.getBooker().getId()),
                UserDto.fromUserDto(dto.getBooker()), dto.getStatus());
    }
}