package k6.gatherlove.auth.strategy;

import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.Role;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    @Override
    public User register(RegisterUserRequest request) {
        // build a new User entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                // always give new users the USER role:
                .roles(Set.of(Role.ROLE_USER))
                .build();

        return userRepository.save(user);
    }
}
