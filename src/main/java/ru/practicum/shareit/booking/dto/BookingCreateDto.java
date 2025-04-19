package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingCreateDto {
    private Long id;

    private Long bookerId;

    @NotNull(message = "ItemId can not be null")
    private Long itemId;

    @JsonProperty("start")
    @Future(message = "StartDate must be in the future")
    @NotNull(message = "StartDate can not be null")
    private LocalDateTime startDate;

    @JsonProperty("end")
    @Future(message = "EndDate must be in the future")
    @NotNull(message = "EndDate can not be null")
    private LocalDateTime endDate;

    private BookingStatus status;
}
