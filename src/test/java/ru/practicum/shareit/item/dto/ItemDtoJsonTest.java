package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testToItemDto() {
        ItemDto itemDto = new ItemDto(
                1,
                "tool",
                "some tool",
                true,
                1);
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("tool");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("some tool");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}