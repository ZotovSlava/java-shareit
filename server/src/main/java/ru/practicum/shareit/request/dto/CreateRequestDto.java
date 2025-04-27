package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateRequestDto {
    private Long id;

    private Long requesterId;

    private String description;

    private LocalDateTime creationDate;
}
