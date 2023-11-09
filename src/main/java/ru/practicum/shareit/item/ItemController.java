package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
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
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto dto, @RequestHeader(header) Integer owner) {
        return service.create(dto, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @PathVariable("itemId") Integer id, @RequestBody ItemDto itemDto,
                          @RequestHeader(header) Integer owner) {
        return service.update(itemDto, owner, id);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader(header) Integer owner, @PathVariable("itemId") Integer id) {
        return service.getItemById(id, owner);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllByUser(@RequestHeader(header) Integer owner) {
        return service.getItemsByUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByText(@RequestParam(value = "text") String text) {
        return service.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestHeader(header) Integer userId,
                              @Valid @RequestBody CommentDto commentDto,
                              @PathVariable("itemId") Integer itemId) {
        return service.addComment(userId, itemId, commentDto);
    }
}