package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithBookingDateDto {
    private List<CommentRequestDto> comments;

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    @JsonProperty("lastBooking")
    private LocalDateTime lastBookingDate;

    @JsonProperty("nextBooking")
    private LocalDateTime futureBookingDate;
}
