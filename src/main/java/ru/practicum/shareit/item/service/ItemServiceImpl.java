package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemStorage;
    @Autowired
    private UserRepository userStorage;
    @Autowired
    private CommentRepository commentStorage;
    @Autowired
    private BookingRepository bookingStorage;

    @Override
    @Transactional
    public ItemDto create(ItemDto i, Integer owner) {
        if (!userStorage.existsById(owner)) {
            throw new NotFoundException(String.format("There is no user with id = %s." +
                    " Item cannot be added to unknown user", owner));
        }
        return ItemDto.toItemDto(itemStorage.save(ItemDto.fromItemDto(i, owner)));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto i, Integer owner, Integer id) {
        if (!itemStorage.existsById(id)) {
            throw new NotFoundException(String.format("There is no user with id = %s." +
                    " Item cannot be added to unknown user", owner));
        }
        if (!itemStorage.existsById(id)) {
            throw new NotFoundException(String.format("There is no item with id = %s." +
                    " Item cannot be added to unknown user", id));
        }
        Item item = itemStorage.getReferenceById(id);
        if (!item.getOwnerId().equals(owner)) {
            throw new NotFoundException(String.format("The item doesn't belong to the user with id = %s.", owner));
        }
        if (i.getName() != null) {
            item.setName(i.getName());
        }
        if (i.getDescription() != null) {
            item.setDescription(i.getDescription());
        }
        if (i.getAvailable() != null) {
            item.setAvailable(i.getAvailable());
        }
        return ItemDto.toItemDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDtoWithBooking getItemById(Integer id, Integer ownerId) {
        if (id == null || !itemStorage.existsById(id)) {
            throw new NotFoundException(String.format("Item with id %s not found", id));
        }

        User u = userStorage.findById(ownerId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Owner with id %s doesn't exist", ownerId))
                );
        return ItemDtoWithBooking.toItemDtoWithBooking(itemStorage.getReferenceById(id),
                bookingStorage.findByItemOwnerId(ownerId), u, commentStorage.findByItemId(id));
    }

    @Override
    @Transactional
    public List<ItemDtoWithBooking> getItemsByUser(Integer id) {
        User user = userStorage.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get items with non-existent user")
                );
        return itemStorage.findAllByOwnerId(id)
                .stream()
                .map(item -> ItemDtoWithBooking.toItemDtoWithBooking(item,
                        bookingStorage.findByItemId(item.getId()), user,
                        commentStorage.findByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.findAllByText(text)
                .stream()
                .map(ItemDto::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("User with id %s not found and is not allowed to leave comments", userId))
                );
        Item item = itemStorage.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Item with id %s not found, " +
                                "comment cannot be left", itemId)));

        List<Booking> bookings = bookingStorage.findByBookerIdAndItemIdAndEndBefore(userId,
                itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BookingException("User has never booked the item or booking is still actual");
        } else {
            return commentStorage.save(CommentDto.toComment(commentDto, user, item));
        }
    }
}