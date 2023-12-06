package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(Integer ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(Integer itemId, Integer userId, ItemDto itemDto) {
        return patch(String.format("/%s", itemId), userId, itemDto);
    }

    public ResponseEntity<Object> get(Integer itemId, Integer userId) {
        return get(String.format("/%s", itemId), userId);
    }

    public ResponseEntity<Object> getAllByUser(Integer ownerId) {
        return get("/", ownerId);
    }

    public ResponseEntity<Object> getAllByText(String text, Integer ownerId) {
        return get(String.format("/search?text=%s", text), ownerId);
    }

    public ResponseEntity<Object> addComment(Integer userId, CommentDto commentDto, Integer itemId) {
        return post(String.format("/%s/comment", itemId), userId, commentDto);
    }
}
