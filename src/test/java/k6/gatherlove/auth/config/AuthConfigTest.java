// src/test/java/k6/gatherlove/auth/config/AuthConfigTest.java
package k6.gatherlove.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthConfigTest {

    @Test
    void passwordEncoderBeanIsPresent() {
        try (var ctx = new AnnotationConfigApplicationContext(AuthConfig.class)) {
            assertTrue(ctx.containsBean("passwordEncoder"), "passwordEncoder bean should exist");
            var encoder = ctx.getBean("passwordEncoder", Object.class);
            assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        }
    }
}
