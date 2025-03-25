package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long id;

    @NotBlank(message = "Name can not be blank")
    String name;

    @Email(message = "Bad email format")
    @NotBlank(message = "Email can not be blank")
    String email;
}
