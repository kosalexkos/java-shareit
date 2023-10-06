package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemStorage itemStorage;

    @Override
    public ItemDto create(ItemDto i, Integer owner) {
        return ItemDto.toItemDto(itemStorage.save(ItemDto.fromItemDto(i, owner)));
    }

    @Override
    public ItemDto update(ItemDto i, Integer owner, Integer id) {
        return ItemDto.toItemDto(itemStorage.update(ItemDto.fromItemDto(i, owner), id));
    }

    @Override
    public ItemDto getItemById(Integer id) {
        return ItemDto.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemsByUser(Integer id) {
        return itemStorage.getItemsByUser(id).stream()
                .map(ItemDto::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllByText(text)
                .stream()
                .map(ItemDto::toItemDto)
                .collect(Collectors.toList());
    }
}