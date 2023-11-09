package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    Integer id;
    @NotBlank
    String text;
    ItemDto item;
    String authorName;
    LocalDateTime created;

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(ItemDto.toItemDto(comment.getItem()))
                .authorName(comment.getAuthorName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .authorName(author.getName())
                .created(LocalDateTime.now())
                .build();
    }
}
