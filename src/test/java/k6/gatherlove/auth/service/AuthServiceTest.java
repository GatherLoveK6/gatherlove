package k6.gatherlove.auth.service;

import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.model.User.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private final AuthService authService = new AuthService() {
        @Override
        public User register(String username, String password) {
            return new User(1L, username, password, Role.USER);
        }

        @Override
        public String login(String username, String password) {
            return "stub-token";
        }
    };

    @Test
    public void testRegister() {
        User user = authService.register("charlie", "password");
        assertEquals("charlie", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    public void testLogin() {
        String token = authService.login("charlie", "password");
        assertEquals("stub-token", token);
    }
}
