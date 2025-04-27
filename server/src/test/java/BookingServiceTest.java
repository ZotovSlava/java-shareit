import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_whenAllValid_thanReturnBookingRequestDto() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");
        Item item = new Item(2L, user, null, "Phone", "Brand new phone", true);

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                userId,
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        Booking booking = BookingMapper.toEntity(createDtoBooking, user, item);

        BookingRequestDto expectedBooking = new BookingRequestDto(
                0L,
                user,
                item,
                createDtoBooking.getStartDate(),
                createDtoBooking.getEndDate(),
                BookingStatus.WAITING,
                BookingState.WAITING
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingRequestDto bookingRequestDto = bookingService.create(createDtoBooking, userId);

        assertNotNull(bookingRequestDto);
        assertEquals(expectedBooking.getId(), bookingRequestDto.getId());
        assertEquals(expectedBooking.getBooker().getId(), userId);
        assertEquals(expectedBooking.getItem().getId(), item.getId());
        assertEquals(expectedBooking.getStartDate(), bookingRequestDto.getStartDate());
        assertEquals(expectedBooking.getEndDate(), bookingRequestDto.getEndDate());
        assertEquals(expectedBooking.getStatus(), bookingRequestDto.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowNotFoundException() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                userId,
                999L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createDtoBooking, userId));
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowNotFoundException() {
        Long itemId = 2L;
        Long nonExistentUserId = 999L;

        Item item = new Item(itemId, null, null, "Phone", "Brand new phone", true);

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                nonExistentUserId,
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(createDtoBooking, nonExistentUserId)
        );

        assertEquals("User not fount " + nonExistentUserId, exception.getMessage());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowValidationException() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");
        Item item = new Item(2L, user, null, "Phone", "Brand new phone", false);

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                userId,
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> bookingService.create(createDtoBooking, userId));
    }

    @Test
    void createBooking_whenStartDateEqualsEndDate_thenThrowValidationException() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");
        Item item = new Item(2L, user, null, "Phone", "Brand new phone", true);

        LocalDateTime sameTime = LocalDateTime.now();

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                userId,
                item.getId(),
                sameTime,
                sameTime,
                BookingStatus.WAITING
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(createDtoBooking, userId)
        );

        assertEquals("The start date of the booking is the same as the end date of the booking", exception.getMessage());
    }

    @Test
    void getBookingById_whenAllValid_thanReturnBookingRequestDto() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");
        Item item = new Item(2L, user, null, "Phone", "Brand new phone", true);

        BookingCreateDto createDtoBooking = new BookingCreateDto(
                0L,
                userId,
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        Booking booking = BookingMapper.toEntity(createDtoBooking, user, item);

        BookingRequestDto expectedBooking = new BookingRequestDto(
                0L,
                user,
                item,
                createDtoBooking.getStartDate(),
                createDtoBooking.getEndDate(),
                BookingStatus.WAITING,
                BookingState.WAITING
        );

        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));

        BookingRequestDto bookingRequestDto = bookingService.get(userId, 0L);

        assertNotNull(bookingRequestDto);
        assertEquals(expectedBooking.getId(), bookingRequestDto.getId());
        assertEquals(expectedBooking.getBooker().getId(), userId);
        assertEquals(expectedBooking.getItem().getId(), item.getId());
        assertEquals(expectedBooking.getStartDate(), bookingRequestDto.getStartDate());
        assertEquals(expectedBooking.getEndDate(), bookingRequestDto.getEndDate());
        assertEquals(expectedBooking.getStatus(), bookingRequestDto.getStatus());
    }

    @Test
    void getBookingById_whenBookingNotFound_thenThrowNotFoundException() {
        Long userId = 1L;
        Long bookingId = 99L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.get(userId, bookingId));
    }

    @Test
    void getBookingById_whenUserNotBookerOrOwner_thenThrowValidationException() {
        Long userId = 3L;
        Long bookingId = 1L;

        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(2L, owner, null, "Phone", "Brand new phone", true);

        Booking booking = new Booking(
                bookingId,
                booker,
                item,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.get(userId, bookingId));
    }

    @Test
    void approveBooking_whenApprovedTrue_thenStatusApproved() {
        Long userId = 1L;
        Long bookingId = 1L;

        User owner = new User(userId, "Owner", "owner@example.com");
        Item item = new Item(2L, owner, null, "Phone", "Brand new phone", true);
        User booker = new User(3L, "Booker", "booker@example.com");

        Booking booking = new Booking(
                bookingId,
                booker,
                item,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingRequestDto result = bookingService.approve(userId, bookingId, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_whenApprovedFalse_thenStatusRejected() {
        Long userId = 1L;
        Long bookingId = 1L;

        User owner = new User(userId, "Owner", "owner@example.com");
        Item item = new Item(2L, owner, null, "Phone", "Brand new phone", true);
        User booker = new User(3L, "Booker", "booker@example.com");

        Booking booking = new Booking(
                bookingId,
                booker,
                item,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingRequestDto result = bookingService.approve(userId, bookingId, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_whenBookingNotFound_thenThrowNotFoundException() {
        Long userId = 1L;
        Long bookingId = 99L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(userId, bookingId, true));
    }

    @Test
    void approveBooking_whenUserNotOwner_thenThrowValidationException() {
        Long ownerId = 1L;
        Long wrongUserId = 99L;
        Long bookingId = 1L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(2L, owner, null, "Phone", "Brand new phone", true);
        User booker = new User(3L, "Booker", "booker@example.com");

        Booking booking = new Booking(
                bookingId,
                booker,
                item,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(wrongUserId, bookingId, true));
    }

    @Test
    void getAllByBooker_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(userId, BookingState.ALL));
    }

    @Test
    void getAllByOwner_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(userId, BookingState.ALL));
    }

    @Test
    void getAllByOwner_whenNoItems_thenThrowNotFoundException() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(userId, BookingState.ALL));
    }

    @Test
    void getAllByOwner_whenItemsButNoBookings_thenReturnEmpty() {
        Long ownerId = 5L;
        User owner = new User(ownerId, "Carol", "carol@example.com");
        Item item = new Item(50L, owner, null, "X", "x", true);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());

        var result = bookingService.getAllByOwner(ownerId, BookingState.ALL);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByOwner_whenVariousStates_thenFilterAndSort() {
        Long ownerId = 10L;
        User owner = new User(ownerId, "Bob", "bob@example.com");

        Item item1 = new Item(100L, owner, null, "A", "a", true);
        Item item2 = new Item(200L, owner, null, "B", "b", true);

        Booking past = new Booking(1L, new User(2L,"u","u@ex"), item1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);
        Booking future = new Booking(2L, new User(3L,"v","v@ex"), item2,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemId(item1.getId())).thenReturn(List.of(past));
        when(bookingRepository.findAllByItemId(item2.getId())).thenReturn(List.of(future));

        var all = bookingService.getAllByOwner(ownerId, BookingState.ALL);
        assertEquals(2, all.size());
        assertEquals(future.getId(), all.get(0).getId());
        assertEquals(past.getId(),    all.get(1).getId());

        var fut = bookingService.getAllByOwner(ownerId, BookingState.FUTURE);
        assertEquals(1, fut.size());
        assertEquals(future.getId(), fut.get(0).getId());

        var pst = bookingService.getAllByOwner(ownerId, BookingState.PAST);
        assertEquals(1, pst.size());
        assertEquals(past.getId(), pst.get(0).getId());
    }

    @Test
    void getAllByBooker_whenMultipleStates_thenFilterCorrectly() {
        Long userId = 1L;
        User booker = new User(userId, "Alice", "alice@example.com");
        Item item = new Item(2L, booker, null, "Item", "Desc", true);

        Booking past = new Booking(1L, booker, item,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);
        Booking current = new Booking(2L, booker, item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);
        Booking future = new Booking(3L, booker, item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(userId))
                .thenReturn(List.of(past, current, future));

        var all = bookingService.getAllByBooker(userId, BookingState.ALL);
        assertEquals(3, all.size());

        var fut = bookingService.getAllByBooker(userId, BookingState.FUTURE);
        assertEquals(1, fut.size());
        assertEquals(future.getId(), fut.get(0).getId());

        var cur = bookingService.getAllByBooker(userId, BookingState.CURRENT);
        assertEquals(1, cur.size());
        assertEquals(current.getId(), cur.get(0).getId());

        var pastList = bookingService.getAllByBooker(userId, BookingState.PAST);
        assertEquals(1, pastList.size());
        assertEquals(past.getId(), pastList.get(0).getId());
    }
}
