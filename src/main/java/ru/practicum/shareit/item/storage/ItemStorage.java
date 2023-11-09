package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemStorage {
    Item save(Item i);

    Item update(Item i, Integer itemId);

    Item getItemById(Integer id);

    Set<Item> getItemsByUser(Integer userId);

    List<Item> getAllByText(String text);

    void addUser(Integer id);
}