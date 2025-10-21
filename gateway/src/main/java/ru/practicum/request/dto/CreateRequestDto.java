package ru.practicum.request.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Valid
public class CreateRequestDto {
    private Long id;

    @NotNull(message = "UserId can not be null")
    private Long requesterId;

    @NotBlank(message = "Description can not be blank")
    private String description;

    private LocalDateTime creationDate;
}
