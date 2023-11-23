package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = header) Integer userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(name = header) Integer userId,
                                                 @PathVariable(name = "requestId") Integer requestId) {
        return itemRequestClient.get(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader(name = header) Integer userId) {
        return itemRequestClient.getByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(name = header) Integer userId,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 @Positive Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }
}