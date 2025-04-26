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
}
