package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    private final String[] errorMessage = new String[]{
            "Booking with id = %s not found",
            "Item with id = %s not found",
            "Item %s is not available",
            "User with id %s is owner of item",
            "Booking since %s till %s is not available",
            "User with id %s is not owner and cannot confirm the booking",
            "Booking with id %s was already approved",
            "Requester with id %s is neither owner of item nor of booking",
            "Wrong pagination data",
            "Unknown state: UNSUPPORTED_STATUS"
    };

    @Override
    @Transactional
    public BookingDto add(BookingDto bookingDto, Integer userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage[1], bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new BookingException(String.format(errorMessage[2], item.getName()));
        }
        User booker = UserDto.fromUserDto(userService.getUserById(userId));
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException(String.format(errorMessage[3], userId));
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException(String.format(errorMessage[4], bookingDto.getStart(), bookingDto.getEnd()));
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(ItemDto.toItemDto(item));
        bookingDto.setBooker(UserDto.toUserDto(booker));

        Booking booking = bookingRepository.save(BookingDto.fromBookingDto(bookingDto));
        return BookingDto.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Integer bookingId, Integer userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new BookingException(String.format(errorMessage[0], bookingId))
                );
        userService.getUserById(userId);
        Item i = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException(String.format(errorMessage[1], booking.getItem())));

        if (!userId.equals(i.getOwnerId())) {
            throw new NotFoundException(String.format(errorMessage[5], userId));
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingException(String.format(errorMessage[6], bookingId));
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        return BookingDto.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(errorMessage[0], bookingId)));
        userService.getUserById(userId);
        Item i = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException(String.format(errorMessage[1], booking.getItem())));
        if (!booking.getBooker().getId().equals(userId) && !i.getOwnerId().equals(userId)) {
            throw new NotFoundException(String.format(errorMessage[7], userId));
        }
        return BookingDto.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllByBooker(String state, Integer bookerId, Integer from, Integer size) {
        userService.getUserById(bookerId);
        if (from < 0) throw new IllegalArgumentException(errorMessage[8]);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId,
                        start, end, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, end, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, start, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING,
                        pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED,
                        pageRequest);
                break;
            default:
                throw new BookingException(errorMessage[9]);
        }

        List<BookingDtoResponse> l = bookings
                .stream()
                .map(BookingDtoResponse::toBookingDtoResponse)
                .collect(Collectors.toList());
        return l;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllByOwner(String state, Integer ownerId, Integer from, Integer size) {
        userService.getUserById(ownerId);
        if (from < 0) throw new IllegalArgumentException(errorMessage[8]);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, start, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId,
                        start, end, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, end, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED, pageRequest);
                break;
            default:
                throw new BookingException(errorMessage[9]);
        }
        List<BookingDtoResponse> l = bookings
                .stream()
                .map(BookingDtoResponse::toBookingDtoResponse)
                .collect(Collectors.toList());
        return l;
    }
}