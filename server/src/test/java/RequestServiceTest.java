import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void createRequest_whenValid_thenReturnRequestDto() {
        User requester = new User(1L, "Grey", "grey@example.com");

        CreateRequestDto createRequestDto = new CreateRequestDto(
                null,
                requester.getId(),
                "Need a laptop",
                null
        );

        Request requestEntity = new Request(
                1L,
                requester,
                createRequestDto.getDescription(),
                LocalDateTime.now()
        );

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(Request.class))).thenReturn(requestEntity);

        RequestDto result = requestService.create(createRequestDto, requester.getId());

        assertNotNull(result);
        assertEquals(requestEntity.getId(), result.getId());
        assertEquals(requestEntity.getRequester().getId(), result.getRequester().getId());
        assertEquals(requestEntity.getDescription(), result.getDescription());
        assertNotNull(result.getCreationDate());
    }

    @Test
    void createRequest_whenUserNotFound_thenThrowNotFoundException() {
        CreateRequestDto createRequestDto = new CreateRequestDto(
                null,
                99L,
                "Need a laptop",
                null
        );

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(createRequestDto, 99L));
    }

    @Test
    void getAllRequests_whenRequestsExist_thenReturnFilteredRequests() {
        Long requesterId = 1L;

        User user1 = new User(1L, "User1", "user1@example.com");
        User user2 = new User(2L, "User2", "user2@example.com");

        Request ownRequest = new Request(1L, user1, "Own request", LocalDateTime.now());
        Request otherRequest1 = new Request(2L, user2, "Other request 1", LocalDateTime.now());
        Request otherRequest2 = new Request(3L, user2, "Other request 2", LocalDateTime.now());

        List<Request> allRequests = List.of(ownRequest, otherRequest1, otherRequest2);

        when(requestRepository.findAll()).thenReturn(allRequests);

        List<RequestDto> result = requestService.getAll(requesterId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(dto -> dto.getRequester().getId().equals(requesterId)));
    }

    @Test
    void getRequestsWithAnswers_whenUserExists_thenReturnRequestsWithItems() {
        Long requesterId = 1L;

        User requester = new User(requesterId, "John", "john@example.com");

        Request request = new Request(1L, requester, "Need a drill", LocalDateTime.now());
        List<Request> requests = List.of(request);

        Item item = new Item(1L, requester, null, "Drill", "Powerful drill", true);
        List<Item> items = List.of(item);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterId(requesterId)).thenReturn(requests);
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        List<RequestWithAnswersDto> result = requestService.get(requesterId);

        assertNotNull(result);
        assertEquals(1, result.size());

        RequestWithAnswersDto requestDto = result.get(0);
        assertEquals(request.getId(), requestDto.getId());
        assertEquals(requesterId, requestDto.getRequester().getId());
        assertEquals(1, requestDto.getAnswers().size());
        assertEquals(item.getId(), requestDto.getAnswers().get(0).getItemId());
        assertEquals(item.getName(), requestDto.getAnswers().get(0).getName());
    }

    @Test
    void getRequestsWithAnswers_whenUserNotFound_thenThrowNotFoundException() {
        Long requesterId = 1L;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.get(requesterId));
    }

    @Test
    void getRequestById_whenRequestExists_thenReturnRequestWithAnswers() {
        Long requestId = 1L;

        User requester = new User(1L, "John", "john@example.com");
        Request request = new Request(requestId, requester, "Need a bike", LocalDateTime.now());

        Item item = new Item(2L, requester, null, "Bike", "Mountain bike", true);
        List<Item> items = List.of(item);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        RequestWithAnswersDto result = requestService.getById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(requester.getId(), result.getRequester().getId());
        assertEquals(1, result.getAnswers().size());
        assertEquals(item.getId(), result.getAnswers().get(0).getItemId());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowNotFoundException() {
        Long requestId = 1L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getById(requestId));
    }
}
