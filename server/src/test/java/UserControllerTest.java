import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createUser_shouldReturnUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Donald Trump", "TheBest100@example.com");
        Mockito.when(userService.create(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void createUser_whenServiceThrows_thenInternalServerError() throws Exception {
        UserDto userDto = new UserDto(1L, "Name", "email@example.com");
        Mockito.when(userService.create(any())).thenThrow(new RuntimeException("Unexpected error"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Donald Trump", "TheBest100@example.com");
        Mockito.when(userService.get(1L)).thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUser_whenNotFound_thenReturns404() throws Exception {
        Mockito.when(userService.get(2L)).thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto updateDto = new UserDto(null, "Updated Name", "updated@email.com");
        UserDto resultDto = new UserDto(1L, "Updated Name", "updated@email.com");
        Mockito.when(userService.update(any(), eq(1L))).thenReturn(resultDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.name").value(resultDto.getName()))
                .andExpect(jsonPath("$.email").value(resultDto.getEmail()));
    }

    @Test
    void updateUser_whenValidationException_thenReturns400() throws Exception {
        UserDto updateDto = new UserDto(null, null, null);
        Mockito.when(userService.update(any(), eq(1L)))
                .thenThrow(new ValidationException("Invalid data"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        Mockito.verify(userService).delete(1L);
    }

    @Test
    void deleteUser_whenValidationException_thenReturns400() throws Exception {
        doThrow(new ValidationException("User ID can not be null"))
                .when(userService).delete(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User ID can not be null"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
