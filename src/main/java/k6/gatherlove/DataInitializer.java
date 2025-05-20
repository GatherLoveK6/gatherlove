package k6.gatherlove;

import k6.gatherlove.auth.model.Role;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Bean
    public ApplicationRunner initAdmin() {
        return args -> {
            String email = "kanye@gmail.com";
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = User.builder()
                        .fullName("Kanye West")
                        .email(email)
                        .username("adminkanye")
                        .phone("+62 3718 273 12")
                        .address("jl depok fasilkom")
                        .password(encoder.encode("secret123"))
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                userRepository.save(admin);
                System.out.println("→ Created admin “kanye@gmail.com” / “secret123”");
            }
        };
    }
}
