package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;


/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer owner;
    Integer request;
}