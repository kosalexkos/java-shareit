package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto i, Integer owner);

    ItemDto update(ItemDto i, Integer ownerId, Integer itemId);

    ItemDtoWithBooking getItemById(Integer id, Integer ownerId);

    List<ItemDtoWithBooking> getItemsByUser(Integer id, Integer from, Integer size);

    List<ItemDto> getItemsByText(String text, Integer from, Integer size);

    Comment addComment(Integer userId, Integer itemId, CommentDto commentDto);
}