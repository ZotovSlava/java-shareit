package ru.practicum.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingCreateDto {
    @NotNull(message = "ItemId can not be null")
    private Long itemId;

    @JsonProperty("start")
    @FutureOrPresent(message = "StartDate must be in the future")
    @NotNull(message = "StartDate can not be null")
    private LocalDateTime startDate;

    @JsonProperty("end")
    @Future(message = "EndDate must be in the future")
    @NotNull(message = "EndDate can not be null")
    private LocalDateTime endDate;
}
