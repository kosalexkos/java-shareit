package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceIntegrationTest {
    private final RequestService requestService;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto shortItemRequestDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "dude", "dude@dude.com");
        userService.create(userDto);
        shortItemRequestDto = new ItemRequestDto(null,"tool",null,null);
        itemRequestDto = requestService.add(shortItemRequestDto, 1);
    }

    @Test
    void shouldAdd() {
        assertNotNull(itemRequestDto.getId());
        assertEquals(shortItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void shouldGetByUserId() {
        List<ItemRequestDto> itemRequestDtoList = requestService.getByUserId(1);
        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    void shouldGetAllRequests() {
        userService.create(new UserDto(null, "dude2", "dude2@user.com"));
        requestService.add(new ItemRequestDto(null,"tool",null,null), 1);
        List<ItemRequestDto> itemRequestDtoList = requestService.getAll(2, 0, 10);
        requestService.getAll(2, 1, 10);

        assertNotNull(itemRequestDtoList);
        assertEquals(2, itemRequestDtoList.size());
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto actualItemRequestDto = requestService.get(1, 1);
        assertNotNull(itemRequestDto);
        assertEquals(itemRequestDto.getId(), actualItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getItems(), actualItemRequestDto.getItems());
    }
}
