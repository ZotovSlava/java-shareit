package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CreateRequestDto createRequestDto) {

        return requestService.create(createRequestDto, userId);

    }

    @GetMapping
    public List<RequestWithAnswersDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.get(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public RequestWithAnswersDto getById(@PathVariable Long requestId) {
        return requestService.getById(requestId);
    }
}
