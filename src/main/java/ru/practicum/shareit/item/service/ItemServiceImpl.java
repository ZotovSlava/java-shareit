package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        userService.get(userId);

        return itemMapper.toDto(itemStorage.create(itemMapper.toItem(itemDto, 0L, userId)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userService.get(userId);

        ItemDto itemSupport = get(itemId);
        ItemDto itemDto1 = new ItemDto(
                itemId,
                itemDto.getName() == null ? itemSupport.getName() : itemDto.getName(),
                itemDto.getDescription() == null ? itemSupport.getDescription() : itemDto.getDescription(),
                itemDto.getAvailable() == null ? itemSupport.getAvailable() : itemDto.getAvailable()
        );

        return itemStorage.update(itemMapper.toItem(itemDto1, itemId, userId))
                .map(itemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public ItemDto get(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Item ID can not be null");
        }

        return itemStorage.get(itemId)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public ItemDto delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Item ID can not be null");
        }

        return itemStorage.delete(itemId)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        if (userId == null) {
            throw new NotFoundException("User ID can not be null");
        }

        userService.get(userId);

        return itemStorage.getAllUserItems(userId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.search(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
