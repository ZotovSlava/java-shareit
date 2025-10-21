import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestWithBookingDateDto;
import ru.practicum.shareit.item.dto.ItemRequestWithCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnItem() throws Exception {
        Long userId = 1L;
        ItemCreateDto createDto = new ItemCreateDto(null, null, "item1", "desc", true);
        ItemRequestDto responseDto = new ItemRequestDto(1L, "item1", "desc", true);

        Mockito.when(itemService.create(any(), eq(userId))).thenReturn(responseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()));
    }


    @Test
    void getItem_shouldReturnItemWithBooking() throws Exception {
        Long itemId = 1L;
        ItemRequestWithBookingDateDto dto = new ItemRequestWithBookingDateDto(
                List.of(), itemId, "ItemName", "desc", true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(itemService.get(itemId)).thenReturn(dto);

        mvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("ItemName"))
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());
    }

    @Test
    void getAllUserItems_shouldReturnList() throws Exception {
        Long userId = 3L;
        ItemRequestWithCommentsDto dto = new ItemRequestWithCommentsDto(
                List.of(), 1L, "Item 1", "Desc", true
        );

        Mockito.when(itemService.getAllUserItems(userId)).thenReturn(List.of(dto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Item 1"));
    }

    @Test
    void search_shouldReturnFoundItems() throws Exception {
        String text = "item";
        ItemRequestDto found = new ItemRequestDto(1L, "item", "desc", true);

        Mockito.when(itemService.search(text)).thenReturn(List.of(found));

        mvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        Long itemId = 1L;
        Long userId = 2L;
        ItemCreateDto updateDto = new ItemCreateDto(itemId, null, "Updated", "Updated desc", true);
        ItemRequestDto response = new ItemRequestDto(itemId, "Updated", "Updated desc", true);

        Mockito.when(itemService.update(any(), eq(itemId), eq(userId))).thenReturn(response);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteItem_shouldReturnOk() throws Exception {
        mvc.perform(delete("/items/1"))
                .andExpect(status().isOk());

        Mockito.verify(itemService).delete(1L);
    }
}
