package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId, PageRequest request);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer bookerId, Status status, PageRequest request);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Integer bookerId, LocalDateTime start,
                                                             LocalDateTime end, PageRequest request);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start,
                                                                PageRequest request);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end,
                                                               PageRequest request);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer owner, PageRequest request);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer owner, LocalDateTime start, PageRequest request);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer owner, Status bookingStatus, PageRequest request);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Integer owner, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end, PageRequest request);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Integer bookerId, Integer itemId, LocalDateTime time);

    List<Booking> findByItemOwnerId(Integer ownerId);

    List<Booking> findByItemId(Integer id);
}