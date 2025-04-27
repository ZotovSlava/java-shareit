package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemCreateDto itemCreateDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemCreateDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRequestDto createComment(@RequestBody CommentCreateDto commentCreateDto,
                                           @PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createComment(commentCreateDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemRequestDto update(@RequestBody ItemCreateDto itemCreateDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId) {
        return itemService.update(itemCreateDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemRequestWithBookingDateDto get(@PathVariable Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemRequestWithCommentsDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }
}
