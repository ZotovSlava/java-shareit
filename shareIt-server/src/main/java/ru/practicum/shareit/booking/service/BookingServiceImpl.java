package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingRequestDto create(BookingCreateDto bookingCreateDto, Long userId) {

        if (bookingCreateDto.getStartDate().equals(bookingCreateDto.getEndDate())) {
            throw new ValidationException("The start date of the booking is the same as the end date of the booking");
        }

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found id: " + bookingCreateDto.getItemId()));

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not fount " + userId));

        if (!item.getAvailable()) {
            throw new ValidationException("The item is not available for booking");
        }

        bookingCreateDto.setStatus(BookingStatus.WAITING);

        return BookingMapper.toRequestDto(
                bookingRepository.save(
                        BookingMapper.toEntity(bookingCreateDto, booker, item)
                )
        );
    }

    @Override
    public BookingRequestDto approve(Long userId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not fount " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("User is not the owner of this item id " + userId);
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toRequestDto(bookingRepository.save(booking));
    }

    @Override
    public BookingRequestDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("The user cannot get access this booking as he is not a member of it");
        }

        return BookingMapper.toRequestDto(booking);
    }

    @Override
    public List<BookingRequestDto> getAllByBooker(Long userId, BookingState state) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not fount " + userId));

        return bookingRepository.findAllByBookerId(userId)
                .stream()
                .map(BookingMapper::toRequestDto)
                .filter(bookingRequestDto -> {
                    if (state == BookingState.ALL) {
                        return true;
                    }

                    return bookingRequestDto.getState() == state;
                })
                .sorted(Comparator.comparing(BookingRequestDto::getStartDate).reversed())
                .toList();
    }

    @Override
    public List<BookingRequestDto> getAllByOwner(Long userId, BookingState state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not fount " + userId));

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            throw new NotFoundException("The user does not have the items");
        }

        return items.stream()
                .map(item -> bookingRepository.findById(item.getOwner().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BookingMapper::toRequestDto)
                .filter(bookingRequestDto -> {
                    if (state == BookingState.ALL) {
                        return true;
                    }

                    return bookingRequestDto.getState() == state;
                })
                .sorted(Comparator.comparing(BookingRequestDto::getStartDate).reversed())
                .toList();
    }
}
