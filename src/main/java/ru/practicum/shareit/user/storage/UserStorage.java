package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    User create(User user);

    Optional<User> update(User user);

    Optional<User> get(Long userId);

    Optional<User> delete(Long userId);
}
