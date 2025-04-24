import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.request.dto.CreateRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = ru.practicum.ShareItGateway.class)
@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private CreateRequestDto createRequestDto;

    @BeforeEach
    void setup() {
        createRequestDto = new CreateRequestDto(
                null, 1L, "Valid description", LocalDateTime.now()
        );
    }

    @Test
    void testCreateRequestDtoSerialization() throws Exception {
        String json = objectMapper.writeValueAsString(createRequestDto);

        assertEquals(true, json.contains("requesterId"));
        assertEquals(true, json.contains("description"));
        assertEquals(true, json.contains("creationDate"));
    }

    @Test
    void testCreateRequestDtoDeserialization() throws Exception {
        String json = "{\"id\":null,\"requesterId\":1,\"description\":\"Valid description\",\"creationDate\":\"2025-04-24T19:15:47\"}";

        CreateRequestDto deserializedDto = objectMapper.readValue(json, CreateRequestDto.class);

        assertEquals(1L, deserializedDto.getRequesterId());
        assertEquals("Valid description", deserializedDto.getDescription());
    }
}
