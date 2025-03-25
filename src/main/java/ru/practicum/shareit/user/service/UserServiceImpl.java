package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    private final Set<String> emailsSet = new HashSet<>();

    @Override
    public UserDto create(UserDto userDto) {
        if (!emailsSet.add(userDto.getEmail())) {
            throw new ConflictException("Такой email уже существует" + userDto.getEmail());
        }

        return userMapper.toUserDto(userStorage.create(userMapper.toUser(userDto, userDto.getId())));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        if (userDto.getEmail() != null && !emailsSet.add(userDto.getEmail())) {
            throw new ConflictException("Такой email уже существует");
        }

        UserDto existingUser = get(userId);
        UserDto updatedUserDto = new UserDto(
                userId,
                userDto.getName() == null ? existingUser.getName() : userDto.getName(),
                userDto.getEmail() == null ? existingUser.getEmail() : userDto.getEmail()
        );


        return userStorage.update(userMapper.toUser(updatedUserDto, userId))
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDto get(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID can not be null");
        }

        return userStorage.get(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID can not be null");
        }

        emailsSet.remove(get(userId).getEmail());

        return userStorage.delete(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
