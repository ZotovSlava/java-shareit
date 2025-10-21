package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentCreateDto {
    private Long id;

    private String text;

    private Long userId;

    private Long itemId;

    private LocalDateTime commentDate;
}

