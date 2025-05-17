// src/test/java/k6/gatherlove/auth/model/UserTest.java
package k6.gatherlove.auth.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void getUsernameReturnsEmail() {
        User u = User.builder()
                .email("foo@bar")
                .password("pw")
                .roles(Set.of(Role.ROLE_USER))
                .build();
        assertThat(u.getUsername()).isEqualTo("foo@bar");
    }

    @Test
    void getPasswordReturnsPassword() {
        User u = new User();
        u.setPassword("secret");
        assertThat(u.getPassword()).isEqualTo("secret");
    }

    @Test
    void authoritiesMatchRoles() {
        User u = User.builder()
                .email("e")
                .password("p")
                .roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .build();

        // compare on the authority strings, not the objects themselves
        assertThat(u.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void defaultRolesEmpty() {
        User u = new User();
        // builder default ensures roles != null
        assertThat(u.getRoles()).isNotNull().isEmpty();
        assertThat(u.getAuthorities()).isEmpty();
    }

    @Test
    void accountFlagsAreAllTrue() {
        User u = new User();
        assertThat(u.isAccountNonExpired()).isTrue();
        assertThat(u.isAccountNonLocked()).isTrue();
        assertThat(u.isCredentialsNonExpired()).isTrue();
        assertThat(u.isEnabled()).isTrue();
    }
}
