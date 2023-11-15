package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(ItemRequestDto dto, Integer userId);

    ItemRequestDto get(Integer userId, Integer requestId);

    List<ItemRequestDto> getByUserId(Integer userId);

    List<ItemRequestDto> getAll(Integer userId, Integer from, Integer size);
}