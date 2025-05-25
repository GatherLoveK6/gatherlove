// src/test/java/k6/gatherlove/auth/repository/UserRepositoryTest.java
package k6.gatherlove.auth.repository;

import k6.gatherlove.auth.model.Role;
import k6.gatherlove.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("save + findByEmail")
    void saveAndFindByEmail() {
        User u = User.builder()
                .fullName("Alice Example")
                .email("alice@example.com")
                .username("alice123")
                .phone("12345")
                .address("123 Main St")
                .password("secret")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        User saved = userRepository.save(u);

        Optional<User> fetched = userRepository.findByEmail("alice@example.com");
        assertThat(fetched)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getId()).isEqualTo(saved.getId());
                    assertThat(found.getEmail()).isEqualTo("alice@example.com");
                    assertThat(found.getFullName()).isEqualTo("Alice Example");
                    assertThat(found.getRoles())
                            .containsExactly(Role.ROLE_USER);
                });
    }

    @Test
    @DisplayName("findByEmail missing → empty")
    void findByEmailWhenMissing() {
        assertThat(userRepository.findByEmail("doesnot@exist")).isEmpty();
    }


}
