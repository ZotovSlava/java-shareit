package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.AnswersDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;
import ru.practicum.shareit.request.mapper.AnswersMapper;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto create(CreateRequestDto createRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        createRequestDto.setCreationDate(LocalDateTime.now());

        return RequestMapper.toDto(
                requestRepository.save(RequestMapper.toEntity(createRequestDto, user))
        );
    }

    @Override
    public List<RequestWithAnswersDto> get(Long requesterId) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<RequestWithAnswersDto> requestsWithAnswersDto = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByRequesterId(requesterId);

        for (Request request : requests) {
            List<Item> items = itemRepository.findAllByRequestId(request.getId());

            List<AnswersDto> answersDto = items
                    .stream()
                    .map(AnswersMapper::toDto)
                    .toList();

            RequestWithAnswersDto requestWithAnswersDto = RequestMapper.toWithAnswersDto(request,  answersDto);

            requestsWithAnswersDto.add(requestWithAnswersDto);
        }

        return requestsWithAnswersDto;
    }

    @Override
    public List<RequestDto> getAll(Long requesterId) {
        return requestRepository.findAll()
                .stream()
                .filter(request -> !request.getRequester().getId().equals(requesterId))
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    public RequestWithAnswersDto getById(Long requestId) {
        List<AnswersDto> answers = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(AnswersMapper::toDto)
                .toList();

        return RequestMapper.toWithAnswersDto(
                requestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Request not found")),
                answers
        );
    }
}
