package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private final String header = "X-Sharer-User-Id";
    private final String path = "/{bookingId}";

    @PostMapping
    public BookingDto add(@RequestBody BookingDto bookingDto, @RequestHeader(header) Integer userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping(path)
    public BookingDto update(@PathVariable Integer bookingId, @RequestHeader(header) Integer userId,
                             @RequestParam Boolean approved) {
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping(path)
    public BookingDto get(@PathVariable Integer bookingId, @RequestHeader(header) Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllByBooker(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestHeader(header) Integer booker,
                                                   @RequestParam(name = "from", defaultValue = "0")
                                                   @PositiveOrZero Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10")
                                                   @Positive Integer size) {
        return bookingService.getAllByBooker(state, booker, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestHeader(header) Integer owner,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                  @PositiveOrZero Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10")
                                                  @Positive Integer size) {
        return bookingService.getAllByOwner(state, owner, from, size);
    }
}