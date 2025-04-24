package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.AnswersDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswersDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Component
public class RequestMapper {

    public static Request toEntity(CreateRequestDto createRequestDto, Long requesterId) {
        return new Request(
                createRequestDto.getId(),
                requesterId,
                createRequestDto.getDescription(),
                createRequestDto.getCreationDate()
        );
    }

    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getRequesterId(),
                request.getDescription(),
                request.getCreationDate()
        );
    }

    public static RequestWithAnswersDto toWithAnswersDto(Request request, List<AnswersDto> answers) {
        return new RequestWithAnswersDto(
                answers,
                request.getId(),
                request.getRequesterId(),
                request.getDescription(),
                request.getCreationDate()
        );
    }
}
