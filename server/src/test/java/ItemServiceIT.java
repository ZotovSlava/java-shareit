import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemRequestWithCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
public class ItemServiceIT {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemService itemService;

    @Test
    void getUserItemTest() {
        User owner = new User(null, "Donny", "don@example.com");
        owner = userRepository.save(owner);

        User requester = new User(null, "Jonny", "joe@example.com");
        requester = userRepository.save(requester);

        User commentator = new User(null, "Huan", "huan@example.com");
        commentator = userRepository.save(commentator);

        Request request = new Request(null, requester, "Need phone", LocalDateTime.now());
        request = requestRepository.save(request);

        Item item = new Item(null, owner, request, "Phone", "Nice", true);
        item = itemRepository.save(item);

        Comment comment = new Comment(null, "Nice", commentator, item, LocalDateTime.now().plusHours(1));
        comment = commentRepository.save(comment);

        List<ItemRequestWithCommentsDto> result = itemService.getAllUserItems(owner.getId());

        assertEquals(1, result.size());
        assertEquals("Phone", result.getFirst().getName());
        assertEquals("Nice", result.getFirst().getDescription());
        assertEquals(1, result.getFirst().getComments().size());
        assertEquals("Nice", result.getFirst().getComments().getFirst().getText());
        assertEquals("Huan", result.getFirst().getComments().getFirst().getAuthorName());
        assertEquals("huan@example.com", result.getFirst().getComments().getFirst().getAuthor().getEmail());
        assertEquals("Phone", result.getFirst().getComments().getFirst().getItem().getName());
        assertEquals(comment.getCommentDate(), result.getFirst().getComments().getFirst().getCommentDate());
        assertEquals(item.getId(), result.getFirst().getComments().getFirst().getItem().getId());
    }
}
