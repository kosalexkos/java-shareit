package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import java.util.Comparator;
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
    private final String [] errorMessage = new String[]{
            "User with id = %s not found ",
            "Item with id = %s not found ",
            "Item cannot be added to unknown user ",
            "Item cannot be updated by unknown user ",
            "Item doesn't belong to the user with id = %s ",
            "User with id %s has never booked the item or booking is still actual "
    };

    @Override
    @Transactional
    public ItemDto create(ItemDto i, Integer owner) {
        if (!userStorage.existsById(owner)) {
            throw new NotFoundException(String.format(errorMessage[0] + errorMessage[2], owner));
        }
        return ItemDto.toItemDto(itemStorage.save(ItemDto.fromItemDto(i, owner)));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto i, Integer owner, Integer id) {
        if (!userStorage.existsById(owner)) {
            throw new NotFoundException(String.format(errorMessage[0] + errorMessage[3], owner));
        }
        if (!itemStorage.existsById(id)) {
            throw new NotFoundException(String.format(errorMessage[1], id));
        }
        Item item = itemStorage.getReferenceById(id);
        if (!item.getOwnerId().equals(owner)) {
            throw new NotFoundException(String.format(errorMessage[4], owner));
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
            throw new NotFoundException(String.format(errorMessage[1], id));
        }

        User u = userStorage.findById(ownerId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage[0], ownerId))
                );
        return ItemDtoWithBooking.toItemDtoWithBooking(itemStorage.getReferenceById(id),
                bookingStorage.findByItemOwnerId(ownerId), u, commentStorage.findByItemId(id));
    }

    @Override
    @Transactional
    public List<ItemDtoWithBooking> getItemsByUser(Integer id, Integer from, Integer size) {
        User user = userStorage.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage[0], id)));
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemStorage.findAllByOwnerId(id, pageRequest)
                .stream()
                .map(item -> ItemDtoWithBooking.toItemDtoWithBooking(item,
                        bookingStorage.findByItemId(item.getId()), user,
                        commentStorage.findByItemId(item.getId())))
                .sorted(Comparator.comparingInt(ItemDtoWithBooking::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByText(String text, Integer from, Integer size) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemStorage.findAllByText(text, pageRequest)
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
                                String.format(errorMessage[0], userId))
                );
        Item item = itemStorage.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage[1], itemId)));

        List<Booking> bookings = bookingStorage.findByBookerIdAndItemIdAndEndBefore(userId,
                itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BookingException(String.format(errorMessage[5], userId));
        } else {
            return commentStorage.save(CommentDto.toComment(commentDto, user, item));
        }
    }
}