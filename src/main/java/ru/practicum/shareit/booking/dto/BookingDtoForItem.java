package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingDtoForItem {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    Integer bookerId;
    Status bookingStatus;

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return BookingDtoForItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .bookingStatus(booking.getStatus())
                .build();
    }
}
