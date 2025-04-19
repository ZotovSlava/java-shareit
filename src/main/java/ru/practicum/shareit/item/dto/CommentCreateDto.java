package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentCreateDto {
    private Long id;

    @NotBlank(message = "Text can not be blank")
    private String text;

    private Long userId;

    private Long itemId;

    private LocalDateTime commentDate;
}

