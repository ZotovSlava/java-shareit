package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Optional<Item> update(Item item);

    Optional<Item> get(Long itemId);

    Optional<Item> delete(Long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> search(String text);
}
