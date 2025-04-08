package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> userMap = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(generateId());
        userMap.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> update(User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);

            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> get(Long userId) {
        return userMap.containsKey(userId) ? Optional.of(userMap.get(userId)) : Optional.empty();
    }

    @Override
    public Optional<User> delete(Long userId) {
        return userMap.containsKey(userId) ? Optional.of(userMap.remove(userId)) : Optional.empty();
    }

    private Long generateId() {
        Set<Long> setKey = userMap.keySet();
        Long maxId = 0L;

        for (Long key : setKey) {
            if (maxId < key) {
                maxId = key;
            }
        }

        return ++maxId;
    }
}
