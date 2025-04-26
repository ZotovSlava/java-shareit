import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ShareItServer.class)
public class RequestServiceIT {
    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_shouldSaveAndReturnRequestDto() {
        User user = new User(null, "John", "john@example.com");
        User savedUser = userRepository.save(user);

        CreateRequestDto createDto = new CreateRequestDto(
                null,
                savedUser.getId(),
                "Need a drill",
                LocalDateTime.now()
        );

        RequestDto result = requestService.create(createDto, savedUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getRequester().getId()).isEqualTo(savedUser.getId());
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreationDate()).isNotNull();

        var persisted = requestRepository.findById(result.getId()).orElseThrow();
        assertThat(persisted.getRequester().getId()).isEqualTo(savedUser.getId());
        assertThat(persisted.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void create_shouldThrowIfUserNotFound() {
        CreateRequestDto createDto = new CreateRequestDto(
                null,
                9999L,
                "Test",
                LocalDateTime.now()
        );

        assertThrows(NotFoundException.class,
                () -> requestService.create(createDto, 9999L),
                "User not found");
    }
}