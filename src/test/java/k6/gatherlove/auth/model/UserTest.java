package k6.gatherlove.auth.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("securehash");
        user.setRole(User.Role.USER);

        assertEquals(1L, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("securehash", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    public void testUserConstructor() {
        User user = new User(2L, "bob", "hash", User.Role.ADMIN);

        assertEquals(2L, user.getId());
        assertEquals("bob", user.getUsername());
        assertEquals("hash", user.getPassword());
        assertEquals(User.Role.ADMIN, user.getRole());
    }
}
