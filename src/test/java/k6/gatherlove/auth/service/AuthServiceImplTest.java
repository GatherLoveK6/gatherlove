package k6.gatherlove.auth.service;

import k6.gatherlove.auth.model.Role;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setup() {
        // make the save-stub lenient so tests that don't call save() won't fail
        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0, User.class);
                    if (u.getId() == null) {
                        u.setId("generated-id");
                    }
                    return u;
                });
    }

    @Test
    @DisplayName("register(...) should save new user with USER role")
    void registerNewUser() {
        // given
        String username = "newuser";
        String rawPassword = "mypassword";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        User created = authService.register(username, rawPassword);

        // then
        then(userRepository).should().findByUsername(username);
        then(userRepository).should().save(userCaptor.capture());

        User toSave = userCaptor.getValue();
        assertThat(toSave.getUsername()).isEqualTo(username);
        assertThat(toSave.getPassword())
                .isNotBlank()
                .doesNotContain(rawPassword);  // ensure hashing
        assertThat(toSave.getRoles())
                .containsExactlyInAnyOrder(Role.ROLE_USER);

        // and our save‐stub set the ID
        assertThat(created.getId()).isEqualTo("generated-id");
    }

    @Test
    @DisplayName("register(...) with existing username should throw")
    void registerExistingUserFails() {
        given(userRepository.findByUsername("bob"))
                .willReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register("bob", "pw"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already exists");

        then(userRepository).should().findByUsername("bob");
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("login(...) with correct credentials returns token")
    void loginSuccess() {
        // first register "temp" so we have a real hashed password
        String raw = "secret";
        User registered = authService.register("temp", raw);
        String hashed = registered.getPassword();

        // now mock lookup
        User user = new User();
        user.setUsername("temp");
        user.setPassword(hashed);
        given(userRepository.findByUsername("temp")).willReturn(Optional.of(user));

        String token = authService.login("temp", raw);
        assertThat(token).isEqualTo("fake-jwt-token-for-temp");
    }

    @Test
    @DisplayName("login(...) with wrong password should throw")
    void loginWrongPassword() {
        // register "alice" so we have her hash
        String aliceRaw = "correct";
        User registered = authService.register("alice", aliceRaw);
        String hashed = registered.getPassword();

        User user = new User();
        user.setUsername("alice");
        user.setPassword(hashed);
        given(userRepository.findByUsername("alice"))
                .willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login("alice", "wrong"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid password");
    }

    @Test
    @DisplayName("login(...) with missing user should throw")
    void loginMissingUser() {
        given(userRepository.findByUsername("nobody"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nobody", "pw"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}
