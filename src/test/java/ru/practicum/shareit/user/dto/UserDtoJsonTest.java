package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void shouldSerialize() {
        UserDto userDto = new UserDto(1,"dude","dude@dude.com");
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("dude");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("dude@dude.com");
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        String jsonContent = "{\"id\": 1,\"name\": \"dude\", \"email\":\"dude@dude.com\"}";
        UserDto result = this.json.parse(jsonContent).getObject();
        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("dude");
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("dude@dude.com");
    }
}