package k6.gatherlove.auth.repository;

import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.model.User.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByUsername() {
        User user = new User(null, "testuser", "hashedpass", Role.USER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    public void testFindByUsernameNotFound() {
        Optional<User> result = userRepository.findByUsername("ghost");
        assertFalse(result.isPresent());
    }
}
