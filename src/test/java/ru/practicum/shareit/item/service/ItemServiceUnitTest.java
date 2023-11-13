package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1, "dude", "dude@dude.com");
        item = new Item(1, "tool", "some tool", true, user.getId(), null);
        request = new ItemRequest(
                1,
                "request for some tool",
                new User(2, "dude1", "dude1@dude.com"),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldAddItem() {
        when(userRepository.existsById(any(Integer.class))).thenReturn(true);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = itemService.create(new ItemDto(null,
                "tool",
                "some tool",
                true,
                request.getId()), 1);
        assertEquals(itemDto, ItemDto.toItemDto(item));
    }

    @Test
    void shouldNotAddItemWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.create(ItemDto.toItemDto(item), 20));
    }

    @Test
    void shouldThrowsIfAddItemWithUserIdIsNull() {
        assertThrows(NotFoundException.class, () -> itemService.create(ItemDto.toItemDto(item), null));
    }

    @Test
    void shouldUpdateItem() {
        when(itemRepository.existsById(any(Integer.class))).thenReturn(true);
        when(itemRepository.getReferenceById(any(Integer.class))).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.existsById(any(Integer.class))).thenReturn(true);
        ItemDto itemDto = itemService.update(new ItemDto(1,
                "toolUpdated",
                "new some tool",
                false,
                request.getId()), 1, 1);
        assertEquals(itemDto, ItemDto.toItemDto(item));
    }

    @Test
    void shouldNotUpdateNonExistentItem() {
        assertThrows(NotFoundException.class,
                () -> itemService.update(new ItemDto(null,
                        "tool",
                        "new some tool",
                        true,
                        null), 1, 2));
    }

    @Test
    void shouldNotUpdateItemWithNonExistentUser() {
        when(userRepository.existsById(any(Integer.class))).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> itemService.update(new ItemDto(null,
                        "tool",
                        "new some tool",
                        true,
                        null), 1, 2));
    }

    @Test
    void shouldNotUpdateItemByNotOwner() {
        when(itemRepository.existsById(any(Integer.class))).thenReturn(true);
        when(userRepository.existsById(any(Integer.class))).thenReturn(true);
        when(itemRepository.getReferenceById(any(Integer.class))).thenReturn(this.item);
        assertThrows(NotFoundException.class,
                () -> itemService.update(new ItemDto(10,
                        "tool",
                        "new some tool",
                        true,
                        null), 10, 1));
    }

    @Test
    void shouldNotUpdateNullableItem() {
        when(itemRepository.existsById(any(Integer.class))).thenReturn(true);
        when(userRepository.existsById(any(Integer.class))).thenReturn(true);
        when(itemRepository.getReferenceById(any(Integer.class))).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = new ItemDto(null, null, null, false, null);
        ItemDto updated = itemService.update(itemDto, 1, 1);
        assertNotEquals(updated, itemDto);
    }

    @Test
    void shouldThrowIfAskNotExistItem() {
        when(itemRepository.existsById(any(Integer.class))).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.getItemById(2, 1));
    }

    @Test
    void shouldThrowIfAskItemWithNotExistOwner() {
        when(itemRepository.existsById(any(Integer.class))).thenReturn(true);
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItemById(2, 3));
    }

    @Test
    void shouldThrowIfAskAllItemsWithNotExistOwner() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItemsByUser(3, 0, 10));
    }

    @Test
    void shouldGetEmptyListWhenTextBlank() {
        List<ItemDto> itemsDto = itemService.getItemsByText("", 0, 10);
        assertEquals(new ArrayList<>(), itemsDto);
    }

    @Test
    void shouldNotGetAllItemsByNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsByUser(10, 0, 10));
    }

    @Test
    void shouldThrowIfAddCommentByNotExistUser() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(3, 1, new CommentDto()));
    }

    @Test
    void shouldThrowIfAddCommentForNotExistItem() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(3, 2, new CommentDto()));
    }
}
