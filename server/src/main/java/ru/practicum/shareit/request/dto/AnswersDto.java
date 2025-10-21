package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswersDto {
    private Long itemId;
    private Long ownerId;
    private String name;
}
