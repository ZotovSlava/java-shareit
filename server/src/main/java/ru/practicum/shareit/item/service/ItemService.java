package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemRequestDto create(ItemCreateDto itemCreateDto, Long userId);

    CommentRequestDto createComment(CommentCreateDto commentDto, Long userId, Long itemId);

    ItemRequestDto update(ItemCreateDto itemCreateDto, Long itemId, Long userId);

    ItemRequestWithBookingDateDto get(Long itemId);

    void delete(Long itemId);

    List<ItemRequestWithCommentsDto> getAllUserItems(Long userId);

    List<ItemRequestDto> search(String text);
}
