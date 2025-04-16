package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapper {

    public static Booking toEntity(BookingCreateDto bookingCreateDto, User booker, Item item) {
        return new Booking(
                bookingCreateDto.getId(),
                booker,
                item,
                bookingCreateDto.getStartDate(),
                bookingCreateDto.getEndDate(),
                bookingCreateDto.getStatus()
        );
    }

    public static BookingRequestDto toRequestDto(Booking booking) {
        return new BookingRequestDto(
                booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                calculateState(booking)
        );
    }

    private static BookingState calculateState(Booking booking) {

        if (booking.getStatus() == BookingStatus.WAITING) {

            if (booking.getStartDate().isBefore(LocalDateTime.now())) {
                return BookingState.REJECTED;
            } else {
                return BookingState.WAITING;
            }
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {

            if (booking.getStartDate().isAfter(LocalDateTime.now())) {
                return BookingState.FUTURE;
            }

            if (booking.getEndDate().isAfter(LocalDateTime.now())) {
                return BookingState.CURRENT;
            }

            return BookingState.PAST;
        }

        return BookingState.REJECTED;
    }
}
