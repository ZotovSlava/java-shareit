package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentCreateDto;
import ru.practicum.item.dto.ItemCreateDto;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId) {
        return itemClient.get(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemCreateDto itemCreateDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.create(itemCreateDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
                                                @PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createComment(commentCreateDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemCreateDto itemCreateDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId) {
        return itemClient.update(itemCreateDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")                                                           // удалить может любой !!!
    public void delete(@PathVariable Long itemId) {
        itemClient.delete(itemId);
    }
}
