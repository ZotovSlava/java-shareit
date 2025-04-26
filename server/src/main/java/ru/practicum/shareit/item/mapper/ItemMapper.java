package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemMapper {
    public static Item toItem(ItemCreateDto itemCreateDto, User owner, Request request) {
        return new Item(
                itemCreateDto.getId(),
                owner,
                request,
                itemCreateDto.getName(),
                itemCreateDto.getDescription(),
                itemCreateDto.getAvailable()
        );
    }

    public static ItemRequestDto toDto(Item item) {
        return new ItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemRequestWithCommentsDto toItemWithCommentsDto(Item item, List<CommentRequestDto> comments) {

        return new ItemRequestWithCommentsDto(
                comments,
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemRequestWithBookingDateDto toItemWithBookingDateDto(Item item,
                                                                         List<CommentRequestDto> comments,
                                                                         LocalDateTime lastBookingDate,
                                                                         LocalDateTime futureBookingDate) {

        return new ItemRequestWithBookingDateDto(
                comments,
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingDate,
                futureBookingDate
        );
    }
}
