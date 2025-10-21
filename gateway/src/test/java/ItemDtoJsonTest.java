import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.item.dto.ItemCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = ru.practicum.ShareItGateway.class)
@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private ItemCreateDto itemCreateDto;

    @BeforeEach
    void setup() {
        itemCreateDto = new ItemCreateDto("Item name", "Description of the item", true, 1L);
    }

    @Test
    void testItemCreateDtoSerialization() throws Exception {
        String json = objectMapper.writeValueAsString(itemCreateDto);

        assertEquals(true, json.contains("name"));
        assertEquals(true, json.contains("description"));
        assertEquals(true, json.contains("available"));
        assertEquals(true, json.contains("requestId"));
    }

    @Test
    void testItemCreateDtoDeserialization() throws Exception {
        String json = "{\"name\":\"Item name\",\"description\":\"Description of the item\",\"available\":true,\"requestId\":1}";

        ItemCreateDto deserializedDto = objectMapper.readValue(json, ItemCreateDto.class);

        assertEquals("Item name", deserializedDto.getName());
        assertEquals("Description of the item", deserializedDto.getDescription());
        assertEquals(true, deserializedDto.getAvailable());
        assertEquals(1L, deserializedDto.getRequestId());
    }
}
