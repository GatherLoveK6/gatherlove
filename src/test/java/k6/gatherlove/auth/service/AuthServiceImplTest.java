package k6.gatherlove.auth.service;

import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.model.User.Role;
import k6.gatherlove.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    private UserRepository userRepository;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthServiceImpl(userRepository);
    }

    @Test
    void testRegisterSuccess() {
        String username = "testUser";
        String password = "testPass";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = authService.register(username, password);

        assertEquals(username, registered.getUsername());
        assertNotEquals(password, registered.getPassword());
        assertEquals(Role.USER, registered.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateUsername() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.register("john", "1234");
        });

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testLoginSuccess() {
        String username = "john";
        String rawPassword = "pass123";
        String hashedPassword = authService.hashPassword(rawPassword);
        User user = new User(1L, username, hashedPassword, Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String token = authService.login(username, rawPassword);
        assertEquals("fake-jwt-token-for-john", token);
    }

    @Test
    void testLoginInvalidPassword() {
        String username = "john";
        User user = new User(1L, username, authService.hashPassword("correct"), Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login(username, "wrong");
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login("ghost", "pass");
        });

        assertEquals("User not found", exception.getMessage());
    }
}
