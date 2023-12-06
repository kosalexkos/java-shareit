package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBooking {
    Integer id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    Integer ownerId;
    @NotNull
    Boolean available;
    BookingDtoForItem lastBooking;
    BookingDtoForItem nextBooking;
    List<CommentDto> comments;

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, List<Booking> bookings, User user,
                                                          List<Comment> comments) {
        LocalDateTime time = LocalDateTime.now();

        Optional<Booking> lastBooking = bookings.stream()
                .filter(b -> user.getId().equals(b.getItem().getOwnerId()))
                .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                .max(Comparator.comparing(Booking::getId));
        Optional<Booking> nextBooking = bookings.stream()
                .filter(b -> user.getId().equals(b.getItem().getOwnerId()))
                .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                .filter(b -> b.getStart().isAfter(time))
                .min(Comparator.comparing(Booking::getStart));

        BookingDtoForItem actualLastBooking = lastBooking
                .map(BookingDtoForItem::toBookingDtoForItem).orElse(null);
        BookingDtoForItem actualNextBooking = nextBooking
                .map(BookingDtoForItem::toBookingDtoForItem).orElse(null);

        List<CommentDto> commentDtos = comments
                .stream()
                .map(CommentDto::toCommentDto)
                .collect(Collectors.toList());

        return ItemDtoWithBooking
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .available(item.getAvailable())
                .lastBooking(actualLastBooking)
                .nextBooking(actualNextBooking)
                .comments(commentDtos)
                .build();
    }
}