package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequestDto create(ItemCreateDto itemCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        Request request = null;

        if (itemCreateDto.getRequestId() != null) {
            request = requestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }

        return ItemMapper.toDto(itemRepository.save(ItemMapper.toItem(itemCreateDto, user, request)));
    }

    @Override
    public CommentRequestDto createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        boolean isItemRentedByUser = bookingService.getAllByBooker(userId, BookingState.ALL)
                .stream()
                .anyMatch(bookingRequestDto -> bookingRequestDto.getBooker().getId().equals(userId)
                        && bookingRequestDto.getItem().getId().equals(itemId)
                        && (bookingRequestDto.getState() == BookingState.CURRENT
                        || bookingRequestDto.getState() == BookingState.PAST)
                );

        if (!isItemRentedByUser) {
            throw new ValidationException("You have not rented this item");
        }

        commentCreateDto.setCommentDate(LocalDateTime.now());

        Comment comment = CommentMapper.toEntity(commentCreateDto, item, user);

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public ItemRequestDto update(ItemCreateDto itemCreateDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is not the owner of this item");
        }

        if (itemCreateDto.getName() != null) {
            item.setName(itemCreateDto.getName());
        }

        if (itemCreateDto.getDescription() != null) {
            item.setDescription(itemCreateDto.getDescription());
        }

        if (itemCreateDto.getAvailable() != null) {
            item.setAvailable(itemCreateDto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemRequestWithBookingDateDto get(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime lastBookingDate = null;
        LocalDateTime futureBookingDate = null;

        List<BookingRequestDto> bookings = bookingService.getAllByOwner(item.getOwner().getId(), BookingState.ALL)
                .stream()
                .filter(bookingRequestDto -> BookingStatus.APPROVED.equals(bookingRequestDto.getStatus()))
                .toList();

        for (BookingRequestDto bookingRequestDto : bookings) {

            if (bookingRequestDto.getState().equals(BookingState.FUTURE)) {
                if (futureBookingDate == null || futureBookingDate.isBefore(bookingRequestDto.getStartDate())) {
                    futureBookingDate = bookingRequestDto.getStartDate();
                }
            }

            if (bookingRequestDto.getState().equals(BookingState.PAST)
                    || bookingRequestDto.getState().equals(BookingState.CURRENT)) {

                if (lastBookingDate == null || lastBookingDate.isAfter(bookingRequestDto.getEndDate())) {
                    lastBookingDate = bookingRequestDto.getEndDate();
                }
            }
        }

        List<CommentRequestDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();

        return ItemMapper.toItemWithBookingDateDto(item, comments, lastBookingDate, futureBookingDate);
    }

    @Override
    public List<ItemRequestWithCommentsDto> getAllUserItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    List<CommentRequestDto> comments = commentRepository.findAllByItemId(item.getId())
                            .stream()
                            .map(CommentMapper::toDto)
                            .toList();

                    return ItemMapper.toItemWithCommentsDto(item, comments);
                })
                .toList();
    }

    @Override
    public void delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Item ID can not be null");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemRequestDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchByText(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toDto)
                .toList();
    }
}
