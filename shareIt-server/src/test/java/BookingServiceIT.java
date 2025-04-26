import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
public class BookingServiceIT {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingServiceImpl bookingService;

    @Test
    @Transactional
    void getAllByBooker_whenUserHasBookings_thenReturnBookings() {
        User booker = userRepository.save(new User(null, "John", "john@example.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));

        Item item = itemRepository.save(new Item(null, owner, null, "Phone", "New phone", true));

        Booking booking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.ALL);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateIsFuture_thenReturnOnlyFutureBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Item2", "Description", true));

        Booking futureBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateIsCurrent_thenReturnOnlyCurrentBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Item3", "Description", true));

        Booking currentBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateIsPast_thenReturnOnlyPastBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Item4", "Description", true));

        Booking pastBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateIsWaiting_thenReturnOnlyWaitingBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Item5", "Description", true));

        Booking waitingBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                BookingStatus.WAITING
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateIsRejected_thenReturnOnlyRejectedBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Item6", "Description", true));

        Booking rejectedBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                BookingStatus.REJECTED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
    }

    @Test
    @Transactional
    void getAllByOwner_whenStateIsFuture_thenReturnOnlyFutureBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Laptop", "Gaming laptop", true));
        User booker = userRepository.save(new User(null, "John", "john@example.com"));

        bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(BookingState.FUTURE, bookings.get(0).getState());
    }

    @Test
    void getAllByOwner_whenStateIsCurrent_thenReturnCurrentBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Tablet", "Android tablet", true));
        User booker = userRepository.save(new User(null, "Jane", "jane@example.com"));

        Booking currentBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateIsPast_thenReturnPastBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Camera", "DSLR camera", true));
        User booker = userRepository.save(new User(null, "Tom", "tom@example.com"));

        Booking pastBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateIsWaiting_thenReturnWaitingBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Monitor", "4K monitor", true));
        User booker = userRepository.save(new User(null, "Steve", "steve@example.com"));

        Booking waitingBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateIsRejected_thenReturnRejectedBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        Item item = itemRepository.save(new Item(null, owner, null, "Headphones", "Noise-cancelling", true));
        User booker = userRepository.save(new User(null, "Alex", "alex@example.com"));

        Booking rejectedBooking = bookingRepository.save(new Booking(
                null,
                booker,
                item,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6),
                BookingStatus.REJECTED
        ));

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
    }
}
