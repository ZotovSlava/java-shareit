package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private Long owner_id;

    @NotBlank(message = "Item name can not be blank")
    private String name;

    @NotBlank(message = "Description can not be blank")
    private String description;

    @NotNull(message = "Available can not be null")
    private Boolean available;
}
