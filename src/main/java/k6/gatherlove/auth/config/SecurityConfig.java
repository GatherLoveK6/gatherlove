package k6.gatherlove.auth.config;

import k6.gatherlove.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class    SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF, because we’re stateless
                .csrf(csrf -> csrf.disable())

                // Stateless session—no HttpSession
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define which URLs are public
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/",
                                "/auth/login",
                                "/auth/register",
                                "/auth/welcome",
                                "/auth/login.html",
                                "/auth/register.html",
                                "/auth/welcome.html",
                                "/assets/**"
                        ).permitAll()
                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )

                // Disable the default login form and basic auth
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Insert your JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}