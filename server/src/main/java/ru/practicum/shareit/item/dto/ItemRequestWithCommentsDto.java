package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithCommentsDto {
    private List<CommentRequestDto> comments = new ArrayList<>();

    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
