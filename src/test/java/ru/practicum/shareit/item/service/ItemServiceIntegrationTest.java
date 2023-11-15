package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private ItemDto itemDto;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "dude", "dude@dude.com");
        itemDto = new ItemDto(null, "tool", "some tool", true, null);
    }

    @Test
    void shouldCreateItem() {
        UserDto user = userService.create(userDto);
        assertNotNull(user.getId());

        ItemDto item = itemService.create(itemDto, user.getId());
        assertNotNull(item.getId());
        itemDto.setId(item.getId());
        assertEquals(item, itemDto);
    }

    @Test
    void shouldUpdateItem() {
        UserDto user = userService.create(userDto);
        ItemDto item = itemService.create(itemDto, user.getId());
        assertNotNull(item.getId());
        ItemDto updateItem = new ItemDto(null,
                "new tool",
                "new damn tool",
                false,
                null);
        ItemDto actualItem = itemService.update(updateItem, user.getId(), item.getId());
        assertEquals(updateItem.getName(), actualItem.getName());
        assertEquals(updateItem.getDescription(), actualItem.getDescription());
        assertEquals(updateItem.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void shouldGetItemById() {
        UserDto user = userService.create(userDto);
        ItemDto item = itemService.create(itemDto, user.getId());
        assertNotNull(item.getId());
        assertNotNull(itemService.getItemById(1, 1));
    }

    @Test
    void shouldGetItemsByUser() {
        UserDto user = userService.create(userDto);
        itemService.create(itemDto, user.getId());
        itemService.create(new ItemDto(null,
                "tool2",
                "some tool2",
                true,
                null), user.getId());
        assertEquals(2, itemService.getItemsByUser(user.getId(), 0, 2).size());
    }

    @Test
    void shouldGetItemsByText() {
        UserDto user = userService.create(userDto);
        UserDto renter = userService.create(new UserDto(null, "requestor", "requestor@dude.com"));
        itemService.create(itemDto, user.getId());
        ItemDto item1 = itemService.update(new ItemDto(null,
                        null,
                        "some new really good tool",
                        true,
                        null),
                user.getId(), 1);
        ItemDto item2 = itemService.create(new ItemDto(null,
                "another took",
                "another really good tool",
                true,
                null), user.getId());

        assertEquals(List.of(item1, item2), itemService.getItemsByText("REALLY", 0, 10));
    }

    @Test
    void shouldNotAddCommentByStranger() {
        UserDto user = userService.create(userDto);
        ItemDto item = itemService.create(itemDto, user.getId());
        CommentDto comment = new CommentDto(null,
                "bla bla",
                item,
                "real dude",
                LocalDateTime.now());
        assertThrows(BookingException.class, () -> itemService.addComment(user.getId(), item.getId(), comment));
    }

    @Test
    void shouldAddComment() {
        UserDto user = userService.create(userDto);
        UserDto renter = userService.create(new UserDto(null, "requestor", "requestor@dude.com"));
        Item item = itemRepository.save(
                new Item(null, "item", "some item", true, user.getId(), null));
        Booking booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, UserDto.fromUserDto(renter), Status.APPROVED);
        bookingRepository.save(booking);
        itemService.addComment(renter.getId(), item.getId(),
                new CommentDto(null, "some text about some tool", ItemDto.toItemDto(item),
                        "renter", null));
        assertFalse(commentRepository.findByItemId(item.getId()).isEmpty());
    }
}
