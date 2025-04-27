import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = ru.practicum.ShareItGateway.class)
@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setup() {
        bookingCreateDto = new BookingCreateDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    void testBookingCreateDtoSerialization() throws Exception {
        String json = objectMapper.writeValueAsString(bookingCreateDto);

        assertEquals(true, json.contains("itemId"));
        assertEquals(true, json.contains("start"));
        assertEquals(true, json.contains("end"));
    }

    @Test
    void testBookingCreateDtoDeserialization() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2025-04-24T20:15:47\",\"end\":\"2025-04-24T21:15:47\"}";

        BookingCreateDto deserializedDto = objectMapper.readValue(json, BookingCreateDto.class);

        assertEquals(1L, deserializedDto.getItemId());
        assertEquals(LocalDateTime.parse("2025-04-24T20:15:47"), deserializedDto.getStartDate());
        assertEquals(LocalDateTime.parse("2025-04-24T21:15:47"), deserializedDto.getEndDate());
    }
}
