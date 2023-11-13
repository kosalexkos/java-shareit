package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private final RequestService requestService;
    private final String header = "X-Sharer-User-Id";
    private final String path = "/{requestId}";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(name = header) Integer userId,
                                        @RequestBody @Valid ItemRequestDto dto) {
        return requestService.add(dto, userId);
    }

    @GetMapping(path)
    public ItemRequestDto getRequestById(@RequestHeader(name = header) Integer userId,
                                         @PathVariable(name = "requestId") Integer requestId) {
        return requestService.get(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(name = header) Integer userId,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10")
                                               @Positive Integer size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestByUserId(@RequestHeader(name = header) Integer userId) {
        return requestService.getByUserId(userId);
    }
}