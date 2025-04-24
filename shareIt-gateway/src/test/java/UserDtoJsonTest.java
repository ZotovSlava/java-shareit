import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = ru.practicum.ShareItGateway.class)
@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto("Nix", "Nix@example.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Nix");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Nix@example.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String jsonContent = "{\"name\":\"Nix\",\"email\":\"Nix@example.com\"}";

        UserDto parsed = json.parseObject(jsonContent);

        assertThat(parsed.getName()).isEqualTo("Nix");
        assertThat(parsed.getEmail()).isEqualTo("Nix@example.com");
    }
}
