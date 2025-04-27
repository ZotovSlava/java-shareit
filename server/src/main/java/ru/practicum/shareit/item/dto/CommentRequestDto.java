package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRequestDto {

    private Long id;

    private String text;

    private String authorName;

    private User author;

    private Item item;

    @JsonProperty("created")
    private LocalDateTime commentDate;
}
