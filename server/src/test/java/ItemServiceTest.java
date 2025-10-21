import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_whenAllValid_thenReturnItemRequestDto() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemCreateDto itemCreateDto = new ItemCreateDto(
                null,
                requestId,
                "Bike",
                "A mountain bike",
                true
        );

        User user = new User(userId, "John", "john@example.com");
        Request request = new Request(requestId, user, "Request description", LocalDateTime.now());
        Item item = new Item(1L, user, request, "Bike", "A mountain bike", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemRequestDto itemRequestDto = itemService.create(itemCreateDto, userId);

        assertNotNull(itemRequestDto);
        assertEquals("Bike", itemRequestDto.getName());
        assertEquals("A mountain bike", itemRequestDto.getDescription());
        assertTrue(itemRequestDto.getAvailable());
    }

    @Test
    void createItem_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 999L;
        ItemCreateDto itemCreateDto = new ItemCreateDto(
                null,
                1L,
                "Bike",
                "A mountain bike",
                true
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemCreateDto, userId));
    }

    @Test
    void createItem_whenRequestNotFound_thenThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemCreateDto itemCreateDto = new ItemCreateDto(
                null,
                requestId,
                "Bike",
                "A mountain bike",
                true
        );

        User user = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemCreateDto, userId));
    }


    @Test
    void createComment_whenValid_thenCreateComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto(
                null,
                "Great item!",
                1L,
                1L,
                LocalDateTime.now()
        );

        User user = new User(1L, "John", "john@example.com");
        Item item = new Item(1L, user, null, "Phone", "Brand new phone", true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, user, item, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.WAITING, BookingState.PAST);
        when(bookingService.getAllByBooker(anyLong(), any())).thenReturn(Collections.singletonList(bookingRequestDto));

        Comment savedComment = new Comment(1L, "Great item!", user, item, LocalDateTime.now());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentRequestDto commentRequestDto = itemService.createComment(commentCreateDto, 1L, 1L);

        assertNotNull(commentRequestDto);
        assertEquals(commentRequestDto.getText(), commentCreateDto.getText());
        assertEquals(commentRequestDto.getAuthor().getId(), user.getId());
        assertEquals(commentRequestDto.getItem().getId(), item.getId());
    }

    @Test
    void createComment_whenItemNotFound_thenThrowNotFoundException() {

        CommentCreateDto commentCreateDto = new CommentCreateDto(
                null,
                "Great item!",
                1L,
                999L,
                LocalDateTime.now()
        );

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentCreateDto, 999L, 1L));
    }

    @Test
    void createComment_whenUserNotFound_thenThrowNotFoundException() {
        Item item = new Item(1L, null, null, "Phone", "Brand new phone", true);
        CommentCreateDto commentCreateDto = new CommentCreateDto(
                null,
                "Great item!",
                1L,
                1L,
                LocalDateTime.now()
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentCreateDto, 1L, 1L));
    }

    @Test
    void createComment_whenUserHasNotRentedItem_thenThrowValidationException() {
        CommentCreateDto commentCreateDto = new CommentCreateDto(
                null,
                "Great item!",
                1L,
                1L,
                LocalDateTime.now()
        );

        User user = new User(1L, "John", "john@example.com");
        Item item = new Item(1L, user, null, "Phone", "Brand new phone", true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookingService.getAllByBooker(anyLong(), any())).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.createComment(commentCreateDto, 1L, 1L));
    }

    @Test
    void updateItem_whenAllFieldsProvided_thenUpdateAll() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        Item item = new Item(1L, owner, null, "Old Name", "Old Description", true);

        ItemCreateDto itemCreateDto = new ItemCreateDto(null, null, "New Name", "New Description", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto result = itemService.update(itemCreateDto, 1L, owner.getId());

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(false, result.getAvailable());
    }

    @Test
    void updateItem_whenItemNotFound_thenThrowNotFoundException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, null, "New Name", "New Description", true);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(itemCreateDto, 1L, 1L));
    }

    @Test
    void updateItem_whenUserIsNotOwner_thenThrowNotFoundException() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        User anotherUser = new User(2L, "Another", "another@mail.com");
        Item item = new Item(1L, owner, null, "Old Name", "Old Description", true);

        ItemCreateDto itemCreateDto = new ItemCreateDto(null, null, "New Name", "New Description", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(itemCreateDto, 1L, anotherUser.getId()));
    }

    @Test
    void updateItem_whenOnlyNameProvided_thenUpdateName() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        Item item = new Item(1L, owner, null, "Old Name", "Old Description", true);

        ItemCreateDto itemCreateDto = new ItemCreateDto(null, null, "New Name", null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto result = itemService.update(itemCreateDto, 1L, owner.getId());

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals(true, result.getAvailable());
    }

    @Test
    void updateItem_whenOnlyAvailableProvided_thenUpdateAvailable() {
        User owner = new User(1L, "Owner", "owner@mail.com");
        Item item = new Item(1L, owner, null, "Old Name", "Old Description", true);

        ItemCreateDto itemCreateDto = new ItemCreateDto(null, null, null, null, false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto result = itemService.update(itemCreateDto, 1L, owner.getId());

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals(false, result.getAvailable());
    }

    @Test
    void getItem_whenItemNotFound_thenThrowNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.get(1L));

        verify(itemRepository).findById(1L);
    }

    @Test
    void getItem_whenFutureBooking_thenFutureBookingDateUpdated() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, null, "Item", "Desc", true);

        BookingRequestDto futureBooking = new BookingRequestDto(
                1L, owner, item,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                BookingStatus.APPROVED,
                BookingState.FUTURE
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingService.getAllByOwner(owner.getId(), BookingState.ALL))
                .thenReturn(List.of(futureBooking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        ItemRequestWithBookingDateDto result = itemService.get(1L);

        assertEquals(futureBooking.getStartDate(), result.getFutureBookingDate());
        assertNull(result.getLastBookingDate());
    }

    @Test
    void getItem_whenCurrentBooking_thenLastBookingDateUpdated() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, null, "Item", "Desc", true);

        BookingRequestDto currentBooking = new BookingRequestDto(
                1L, owner, item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED,
                BookingState.CURRENT
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingService.getAllByOwner(owner.getId(), BookingState.ALL))
                .thenReturn(List.of(currentBooking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        ItemRequestWithBookingDateDto result = itemService.get(1L);

        assertEquals(currentBooking.getEndDate(), result.getLastBookingDate());
        assertNull(result.getFutureBookingDate());
    }

    @Test
    void delete_whenItemIdIsNull_thenThrowValidationException() {
        assertThrows(ValidationException.class, () -> itemService.delete(null));
        verify(itemRepository, never()).deleteById(any());
    }

    @Test
    void delete_whenItemIdIsNotNull_thenDeleteByIdCalled() {
        Long itemId = 1L;

        itemService.delete(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void search_whenTextIsNull_thenReturnEmptyList() {
        List<ItemRequestDto> result = itemService.search(null);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchByText(anyString());
    }

    @Test
    void search_whenTextIsValid_thenReturnAvailableItems() {
        String text = "test";
        Item item1 = new Item(1L, new User(), null, "Item1", "Description1", true);
        Item item2 = new Item(2L, new User(), null, "Item2", "Description2", false);

        when(itemRepository.searchByText(text)).thenReturn(List.of(item1, item2));

        List<ItemRequestDto> result = itemService.search(text);

        assertEquals(1, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
    }
}
