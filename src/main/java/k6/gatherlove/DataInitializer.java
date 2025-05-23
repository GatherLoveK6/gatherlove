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
    public ApplicationRunner initAdmins() {
        return args -> {
            // Admin 1: Kanye
            String kanyeEmail = "kanye@gmail.com";
            if (userRepository.findByEmail(kanyeEmail).isEmpty()) {
                User kanye = User.builder()
                        .fullName("Kanye West")
                        .email(kanyeEmail)
                        .username("adminkanye")
                        .phone("+62 3718 273 12")
                        .address("Jl. Depok Fasilkom")
                        .password(encoder.encode("secret123"))
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)) // Has both roles
                        .build();
                userRepository.save(kanye);
                System.out.println("→ Created admin “" + kanyeEmail + "” / “secret123”");
            }

            // Admin 2: Admin-only
            String adminEmail = "admin1@gmail.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .fullName("Super Admin")
                        .email(adminEmail)
                        .username("admin123")
                        .phone("+62 811 9999 000")
                        .address("Jl. Admin Center")
                        .password(encoder.encode("adminpass"))
                        .roles(Set.of(Role.ROLE_ADMIN)) // Only admin role
                        .build();
                userRepository.save(admin);
                System.out.println("→ Created admin “" + adminEmail + "” / “adminpass”");
            }
        };
    }
}
