// src/test/java/k6/gatherlove/auth/strategy/UserAuthStrategyTest.java
package k6.gatherlove.auth.strategy;

import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.Role;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.repository.UserRepository;
import k6.gatherlove.service.MetricsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthStrategyTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private UserAuthStrategy strategy;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("login(...) with valid credentials returns user")
    void loginSuccess() {
        String raw = "pw123";
        User stored = new User();
        stored.setEmail("u@e.com");
        String encoded = new BCryptPasswordEncoder().encode(raw);
        stored.setPassword(encoded);

        given(userRepository.findByEmail("u@e.com"))
                .willReturn(Optional.of(stored));

        User result = strategy.login("u@e.com", raw);
        assertThat(result).isSameAs(stored);
    }

    @Test
    @DisplayName("login(...) with wrong password throws")
    void loginWrongPassword() {
        User stored = new User();
        stored.setEmail("u@e.com");
        stored.setPassword(new BCryptPasswordEncoder().encode("correct"));

        given(userRepository.findByEmail("u@e.com"))
                .willReturn(Optional.of(stored));

        assertThatThrownBy(() -> strategy.login("u@e.com", "nope"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("login(...) with missing email throws")
    void loginMissingUser() {
        given(userRepository.findByEmail("x@y.z"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> strategy.login("x@y.z", "pw"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("register(...) builds and saves a new USER")
    void registerNewUser() {
        RegisterUserRequest req = RegisterUserRequest.builder()
                .fullName("Test Name")
                .email("test@ex")
                .username("testu")
                .password("p@ss")
                .phone("555")
                .address("addr")
                .build();

        given(userRepository.save(any(User.class)))
                .willAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId("new-id");
                    return u;
                });

        User created = strategy.register(req);

        then(userRepository).should().save(userCaptor.capture());
        then(metricsService).should().incrementUserRegistration();
        
        User saved = userCaptor.getValue();

        assertThat(saved.getFullName()).isEqualTo("Test Name");
        assertThat(saved.getEmail()).isEqualTo("test@ex");
        assertThat(saved.getUsername()).isEqualTo("test@ex");
        assertThat(saved.getPhone()).isEqualTo("555");
        assertThat(saved.getAddress()).isEqualTo("addr");
        assertThat(saved.getPassword())
                .isNotBlank()
                .doesNotContain("p@ss");
        assertThat(saved.getRoles())
                .containsExactly(Role.ROLE_USER);

        assertThat(created.getId()).isEqualTo("new-id");
    }
}
