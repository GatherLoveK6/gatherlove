// src/test/java/k6/gatherlove/auth/config/SecurityConfigTest.java
package k6.gatherlove.auth.config;

import k6.gatherlove.auth.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    /**
     * Provide a no-op JwtAuthenticationFilter so that SecurityConfig can autowire it.
     */
    @Configuration
    static class TestConfig {
        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(null) {
                @Override
                protected void doFilterInternal(
                        jakarta.servlet.http.HttpServletRequest request,
                        jakarta.servlet.http.HttpServletResponse response,
                        jakarta.servlet.FilterChain filterChain
                ) throws java.io.IOException, jakarta.servlet.ServletException {
                    // no-op: chain through
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    @Test
    void securityFilterChainBeanIsPresent() {
        try (var ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(TestConfig.class, SecurityConfig.class);
            ctx.refresh();

            assertTrue(ctx.containsBean("securityFilterChain"),
                    "securityFilterChain bean should be registered");
            assertInstanceOf(SecurityFilterChain.class,
                    ctx.getBean("securityFilterChain"));
        }
    }

}
