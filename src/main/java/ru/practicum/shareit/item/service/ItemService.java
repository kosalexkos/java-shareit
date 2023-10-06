package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto i, Integer owner);

    ItemDto update(ItemDto i, Integer ownerId, Integer itemId);

    ItemDto getItemById(Integer id);

    List<ItemDto> getItemsByUser(Integer id);

    List<ItemDto> getItemsByText(String text);
}