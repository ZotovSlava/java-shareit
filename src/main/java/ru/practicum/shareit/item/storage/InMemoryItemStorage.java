package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {

    Map<Long, Item> itemsMap = new HashMap();

    @Override
    public Item create(Item item) {
        item.setId(idCounter());
        itemsMap.put(item.getId(), item);

        return item;
    }

    @Override
    public Optional<Item> update(Item item) {
        if (itemsMap.containsKey(item.getId())) {
            itemsMap.put(item.getId(), item);

            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Item> get(Long itemId) {
        return itemsMap.containsKey(itemId) ? Optional.of(itemsMap.get(itemId)) : Optional.empty();
    }

    @Override
    public Optional<Item> delete(Long itemId) {
        return itemsMap.containsKey(itemId) ? Optional.of(itemsMap.remove(itemId)) : Optional.empty();
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        List<Item> userItemsList = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemsMap.values());
        for (Item item : items) {
            if (item.getOwner_id().equals(userId)) {
                userItemsList.add(item);
            }
        }
        return userItemsList;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> searchList = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemsMap.values());

        for (Item item : items) {
            if ((item.getName().toUpperCase().contains(text) || item.getDescription().toUpperCase().contains(text))
                    && item.getAvailable()) {
                searchList.add(item);
            }
        }

        return searchList;
    }

    private Long idCounter() {
        Set<Long> setKey = itemsMap.keySet();
        Long maxId = 0L;

        for (Long key : setKey) {
            if (maxId < key) {
                maxId = key;
            }
        }

        return ++maxId;
    }
}
