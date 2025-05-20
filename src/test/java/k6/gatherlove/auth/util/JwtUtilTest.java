// src/test/java/k6/gatherlove/auth/util/JwtUtilTest.java
package k6.gatherlove.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // needs to be at least 32 chars for HS256
    private static final String SECRET = "0123456789ABCDEF0123456789ABCDEF";
    private static final long EXP_MS = 60 * 60 * 1000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // inject the @Value fields
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", EXP_MS);
        // init the signing key
        jwtUtil.init();
    }

    @Test
    void generateTokenProducesJwtAndValidateReturnsTrue() {
        var user = new User(
                "alice",
                "unused",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtUtil.generateToken(user);
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateTokenRejectsInvalidJwt() {
        assertThat(jwtUtil.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    void getAuthenticationExtractsSubjectAndAuthorities() {
        var user = new User(
                "bob",
                "pwd",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );
        String token = jwtUtil.generateToken(user);

        Authentication auth = jwtUtil.getAuthentication(token);
        assertThat(auth.getName()).isEqualTo("bob");
        assertThat(auth.getCredentials()).isNull();

        // again compare the authority strings
        assertThat(auth.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void expirationMsGetterReturnsConfiguredValue() {
        assertThat(jwtUtil.getExpirationMs()).isEqualTo(EXP_MS);
    }
}
