package k6.gatherlove.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class AuthConfigTest {

    @Test
    void passwordEncoder_shouldReturnNonNullInstance() {
        AuthConfig config = new AuthConfig();
        BCryptPasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();
        assertThat(encoder.encode("password")).isNotEqualTo("password"); // should be hashed
    }
}
