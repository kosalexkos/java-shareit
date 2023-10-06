package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

@RequestMapping("/items")
@RestController
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService service;

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return service.create(dto, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @PathVariable("itemId") Integer id, @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return service.update(itemDto, owner, id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Integer id) {
        return service.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Integer owner) {
        return service.getItemsByUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByText(@RequestParam(value = "text") String text) {
        return service.getItemsByText(text);
    }
}