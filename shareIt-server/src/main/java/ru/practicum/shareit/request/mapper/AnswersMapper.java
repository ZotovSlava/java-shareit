package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnswersDto;

@Component
public class AnswersMapper {

    public static AnswersDto toDto(Item item) {
        return new AnswersDto(
                item.getId(),
                item.getOwner().getId(),
                item.getName()
        );
    }
}
