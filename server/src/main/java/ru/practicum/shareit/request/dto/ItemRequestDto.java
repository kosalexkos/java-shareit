package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-item-requests.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Integer id;
    @NotBlank
    String description;
    LocalDateTime created;
    List<ItemDto> items;

    public static ItemRequest fromItemRequestDto(ItemRequestDto dto, User user) {
        return new ItemRequest(dto.getId(),
                dto.getDescription(),
                user,
                LocalDateTime.now());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items.stream().map(ItemDto::toItemDto).collect(Collectors.toList())
        );
    }
}
