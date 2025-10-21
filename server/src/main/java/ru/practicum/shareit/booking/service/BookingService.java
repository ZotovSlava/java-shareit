package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingRequestDto create(BookingCreateDto bookingCreateDto, Long userId);

    BookingRequestDto approve(Long userId, Long bookingId, Boolean approved);

    BookingRequestDto get(Long userId, Long bookingId);

    List<BookingRequestDto> getAllByBooker(Long userId, BookingState state);

    List<BookingRequestDto> getAllByOwner(Long userId, BookingState state);
}
