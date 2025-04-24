package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;

import java.util.List;

public interface RequestService {
    RequestDto create(CreateRequestDto createRequestDto, Long requesterId);

    List<RequestWithAnswersDto> get(Long requesterId);

    List<RequestDto> getAll(Long requesterId);

    RequestWithAnswersDto getById(Long requestId);
}
