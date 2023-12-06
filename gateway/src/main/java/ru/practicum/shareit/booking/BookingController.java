package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(header) Integer userId,
                                               @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10")
                                               @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(header) Integer ownerId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @RequestParam(name = "from", defaultValue = "0")
                                                @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "10")
                                                @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(header) Integer userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Integer bookingId, @RequestHeader(header) Integer userId,
                                                @RequestParam Boolean approved) {
        log.info("Updating bookingId {}, userId={}", bookingId, userId);
        return bookingClient.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(header) Integer userId,
                                             @PathVariable Integer bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}