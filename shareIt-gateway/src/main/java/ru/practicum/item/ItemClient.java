package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.CommentCreateDto;
import ru.practicum.item.dto.ItemCreateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> get(Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return get("/{itemId}", parameters);
    }

    public ResponseEntity<Object> getAllUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );

        return get("/search?text={text}", parameters);
    }

    public ResponseEntity<Object> create(ItemCreateDto itemCreateDto, Long userId) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return post("/{itemId}/comment", userId, parameters, commentCreateDto);
    }

    public ResponseEntity<Object> update(ItemCreateDto itemCreateDto, Long itemId, Long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return post("/{itemId}", userId, parameters, itemCreateDto);
    }

    public void delete(Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        delete("/{itemId}", parameters);
    }
}
