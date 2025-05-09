package k6.gatherlove.auth.config;

import k6.gatherlove.auth.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void securityFilterChain_shouldNotBeNull() throws Exception {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);
        SecurityFilterChain chain = config.securityFilterChain(null); // null for HttpSecurity is okay in unit test context
        assertThat(chain).isNotNull();
    }
}
