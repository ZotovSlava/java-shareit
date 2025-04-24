package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestWithAnswersDto {
    @JsonProperty("items")
    private List<AnswersDto> answers;
    private Long id;
    private Long requesterId;
    private String description;

    @JsonProperty("created")
    private LocalDateTime creationDate;
}
