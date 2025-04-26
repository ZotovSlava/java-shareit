package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestWithAnswersDto {
    @JsonProperty("items")
    private List<AnswersDto> answers;
    private Long id;
    private User requester;
    private String description;

    @JsonProperty("created")
    private LocalDateTime creationDate;
}
