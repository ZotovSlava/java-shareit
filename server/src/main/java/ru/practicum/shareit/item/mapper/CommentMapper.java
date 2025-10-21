package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static Comment toEntity(CommentCreateDto commentCreateDto, Item item, User user) {
        return new Comment(
                commentCreateDto.getId(),
                commentCreateDto.getText(),
                user,
                item,
                commentCreateDto.getCommentDate()
        );

    }

    public static CommentRequestDto toDto(Comment comment) {
        return new CommentRequestDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getAuthor(),
                comment.getItem(),
                comment.getCommentDate()
        );
    }


}
