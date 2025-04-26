package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto, userDto.getId())));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Данный user не найден"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        user.setId(userId);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto get(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID can not be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Данный user не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID can not be null");
        }

        userRepository.deleteById(userId);
    }
}
