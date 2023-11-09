package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer bookerId, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Integer bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer owner);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer owner, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer owner, Status bookingStatus);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Integer owner, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Integer bookerId, Integer itemId, LocalDateTime time);

    List<Booking> findByItemOwnerId(Integer ownerId);

    List<Booking> findByItemId(Integer id);
}