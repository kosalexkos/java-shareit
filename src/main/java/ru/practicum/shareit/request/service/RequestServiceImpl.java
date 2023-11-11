package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private final String errorMessage = "User with an id = %s not found";
    private final String errorMessage2 = "Request with an id = %s not found";

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestDto dto, Integer userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage, userId)));
        ItemRequest r = ItemRequestDto.fromItemRequestDto(dto, u);
        r.setRequestor(u);
        r.setCreated(LocalDateTime.now());
        return ItemRequestDto.toItemRequestDto(requestRepository.save(r),
                itemRepository.findAllByRequestIdOrderByIdDesc(r.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(Integer userId, Integer requestId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage, userId)));

        ItemRequest r = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage2, requestId))
                );

        return ItemRequestDto.toItemRequestDto(r, itemRepository.findAllByRequestIdOrderByIdDesc(r.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(String.format(errorMessage, userId)));
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return requests
                .stream()
                .map(request -> ItemRequestDto.toItemRequestDto(request,
                        itemRepository.findAllByRequestIdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get request with non-existent user")
                );
        List<ItemRequest> requests = requestRepository.findByRequestorIdIsNotOrderByCreatedDesc(userId,
                PageRequest.of(from > 0 ? from / size : 0, size));
        return requests
                .stream()
                .map(request -> ItemRequestDto.toItemRequestDto(request,
                        itemRepository.findAllByRequestIdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }
}