import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.AnswersDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    private CreateRequestDto createRequestDto;
    private RequestDto responseDto;
    private RequestWithAnswersDto responseWithAnswersDto;

    @BeforeEach
    void setup() {
        createRequestDto = new CreateRequestDto(
                null, 1L, "Request description", LocalDateTime.now()
        );

        responseDto = new RequestDto(
                1L, 1L, "Request description", LocalDateTime.now()
        );

        responseWithAnswersDto = new RequestWithAnswersDto(
                List.of(new AnswersDto(1L, 1L, "Item name")),
                1L, 1L, "Request description", LocalDateTime.now()
        );
    }

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        Mockito.when(requestService.create(any(), eq(1L))).thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    void getRequestsByUser_shouldReturnUserRequests() throws Exception {
        Mockito.when(requestService.get(1L)).thenReturn(List.of(responseWithAnswersDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(responseWithAnswersDto.getDescription())));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        Mockito.when(requestService.getAll(1L)).thenReturn(List.of(responseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())));
    }

    @Test
    void getRequestById_shouldReturnRequestById() throws Exception {
        Mockito.when(requestService.getById(1L)).thenReturn(responseWithAnswersDto);

        mvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(responseWithAnswersDto.getDescription())))
                .andExpect(jsonPath("$.items.size()", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Item name")));
    }
}
