package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingCreateDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );

        return get("/{bookingId}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByBooker(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );

        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );

        return get("/owner?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> create(BookingCreateDto bookingCreateDto, Long userId) {
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );

        return patch("/{bookingId}?approved={approved}", userId, parameters);
    }
}

