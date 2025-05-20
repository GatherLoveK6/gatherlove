package k6.gatherlove.auth.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RoleTest {

    @Test
    void allRolesPresent() {
        // ensure both roles are in the enum
        assertThat(Role.values())
                .containsExactlyInAnyOrder(Role.ROLE_USER, Role.ROLE_ADMIN);
    }

    @Test
    void fromString() {
        assertThat(Role.valueOf("ROLE_USER")).isEqualTo(Role.ROLE_USER);
        assertThat(Role.valueOf("ROLE_ADMIN")).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void namesMatch() {
        assertThat(Role.ROLE_USER.name()).isEqualTo("ROLE_USER");
        assertThat(Role.ROLE_ADMIN.name()).isEqualTo("ROLE_ADMIN");
    }
}
