import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenAllValid_thenReturnSavedUserDto() {
        UserDto createDto = new UserDto(null, "Grey", "crey@example.com"); // Обычно id при создании null
        User savedUser = new User(1L, "Grey", "crey@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.create(createDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenAllValid_thenReturnUpdatedUserDto() {
        Long userId = 1L;
        User existingUser = new User(userId, "Old Name", "old@example.com");
        UserDto updateDto = new UserDto(userId, "New Name", "new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.update(updateDto, userId);

        assertNotNull(updatedUserDto);
        assertEquals(userId, updatedUserDto.getId());
        assertEquals("New Name", updatedUserDto.getName());
        assertEquals("new@example.com", updatedUserDto.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 1L;
        UserDto updateDto = new UserDto(userId, "New Name", "new@example.com");


        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(updateDto, userId));

        assertEquals("Данный user не найден", exception.getMessage());
    }

    @Test
    void updateUser_whenOnlyEmailProvided_thenUpdateOnlyEmail() {
        Long userId = 1L;
        User existingUser = new User(userId, "Old Name", "old@example.com");
        UserDto updateDto = new UserDto(userId, null, "new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.update(updateDto, userId);

        assertNotNull(updatedUserDto);
        assertEquals(userId, updatedUserDto.getId());
        assertEquals("Old Name", updatedUserDto.getName());
        assertEquals("new@example.com", updatedUserDto.getEmail());
    }

    @Test
    void updateUser_whenOnlyNameProvided_thenUpdateOnlyName() {
        Long userId = 1L;
        User existingUser = new User(userId, "Old Name", "old@example.com");
        UserDto updateDto = new UserDto(userId, "New Name", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUserDto = userService.update(updateDto, userId);

        assertNotNull(updatedUserDto);
        assertEquals(userId, updatedUserDto.getId());
        assertEquals("New Name", updatedUserDto.getName());
        assertEquals("old@example.com", updatedUserDto.getEmail());
    }

    @Test
    void getUser_whenUserExists_thenReturnUserDto() {
        User user = new User(1L, "Grey", "grey@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.get(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_whenUserIdIsNull_thenThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.get(null));
        assertEquals("User ID can not be null", exception.getMessage());

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUser_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.get(1L));
        assertEquals("Данный user не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser_whenUserIdValid_thenDeleteSuccessfully() {
        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_whenUserIdIsNull_thenThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.delete(null));
        assertEquals("User ID can not be null", exception.getMessage());

        verify(userRepository, never()).deleteById(any());
    }
}
