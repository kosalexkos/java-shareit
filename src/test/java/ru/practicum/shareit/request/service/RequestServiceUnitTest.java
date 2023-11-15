package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    private User user;
    private User user2;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1, "dude", "dude@user.com");
        user2 = new User(2, "dude2", "dude2@user.com");
        request = new ItemRequest(1, "need a tool", user2, LocalDateTime.now());
    }

    @Test
    void shouldAddRequest() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.save(any())).thenReturn(request);
        when(itemRepository.findAllByRequestIdOrderByIdDesc(any(Integer.class))).thenReturn(List.of());

        assertEquals(ItemRequestDto.toItemRequestDto(request, List.of()),
                requestService.add(new ItemRequestDto(null,
                        request.getDescription(),
                        null,
                        null), user2.getId()));
    }

    @Test
    void shouldNotAddRequestByNonExistentUser() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> requestService.add(new ItemRequestDto(null,
                        request.getDescription(), null, null), 3));
    }

    @Test
    void shouldGetRequestByRequestIdAndUserId() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(request));
        assertDoesNotThrow(() -> requestService.get(user.getId(), request.getId()));
    }

    @Test
    void shouldNotGetRequestWithWrongId() {
        when(userRepository.findById(any(Integer.class))).thenReturn(
                Optional.of(new User(1, "dude", "dude@dude.com")));
        assertThrows(NotFoundException.class, () -> requestService.get(1, 1));
    }

    @Test
    void shouldNotGetRequestWithWrongUserId() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.get(1, 1));
    }

    @Test
    void shouldGetAllByOwner() {
        ItemRequest request2 = new ItemRequest(2, "need another tool", user2, LocalDateTime.now());
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(any(Integer.class)))
                .thenReturn(List.of(request, request2));
        assertEquals(2, requestService.getByUserId(user2.getId()).size());
    }

    @Test
    void shouldNotGetAllRequestsByWrongUser() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getByUserId(1));
    }

    @Test
    void shouldGetAllRequests() {
        ItemRequest request2 = new ItemRequest(2, "need another tool", user, LocalDateTime.now());
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.findByRequestorIdIsNotOrderByCreatedDesc(any(Integer.class), any()))
                .thenReturn(List.of(request2));
        assertEquals(1, requestService.getAll(user2.getId(), 0, 1).size());
    }
}
