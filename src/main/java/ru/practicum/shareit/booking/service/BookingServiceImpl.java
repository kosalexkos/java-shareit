package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    @Transactional
    public BookingDto add(BookingDto bookingDto, Integer userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(
                        () -> new NotFoundException(String.format("Item with id = %s not found", bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new BookingException(String.format("%s is not available at the moment", item.getName()));
        }
        User booker = UserDto.fromUserDto(userService.getUserById(userId));
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Owner is not allowed to book his or her own items");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Booking period is not valid");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(ItemDto.toItemDto(item));
        bookingDto.setBooker(UserDto.toUserDto(booker));

        Booking booking = bookingRepository.save(BookingDto.fromBookingDto(bookingDto));
        System.out.println(booking);
        return BookingDto.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Integer bookingId, Integer userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new BookingException(String.format("Booking with id = %s does not exist", bookingId))
                );
        userService.getUserById(userId);
        Item i = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %s does not exist", booking.getItem())));

        if (!userId.equals(i.getOwnerId())) {
            throw new NotFoundException("No one but the owner can confirm the booking");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingException("Booking was already approved");
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        return BookingDto.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id = %s does not exist", bookingId)));
        userService.getUserById(userId);
        Item i = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %s does not exist", booking.getItem())));
        if (!booking.getBooker().getId().equals(userId) && !i.getOwnerId().equals(userId)) {
            throw new NotFoundException("The requester must be either the owner of the item or the booking");
        }
        return BookingDto.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllByBooker(String state, Integer bookerId) {
        userService.getUserById(bookerId);
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, end);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, start);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                break;
            default:
                throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }

        List<BookingDtoResponse> l = bookings
                .stream()
                .map(BookingDtoResponse::toBookingDtoResponse)
                .collect(Collectors.toList());
        return l;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllByOwner(String state, Integer ownerId) {
        userService.getUserById(ownerId);
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, start);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, end);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
                break;
            default:
                throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<BookingDtoResponse> l = bookings
                .stream()
                .map(BookingDtoResponse::toBookingDtoResponse)
                .collect(Collectors.toList());
        return l;
    }
}