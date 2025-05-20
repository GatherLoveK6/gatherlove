package k6.gatherlove.auth.dto;

import k6.gatherlove.auth.model.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// ← use jakarta.validation now
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class RegisterUserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validRequestHasNoViolations() {
        RegisterUserRequest req = RegisterUserRequest.builder()
                .fullName("Alice")
                .email("alice@example.com")
                .username("alice")
                .password("pass")
                .phone("123")
                .address("road")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        assertThat(validator.validate(req)).isEmpty();
    }

    @Test
    void missingFieldsProduceViolations() {
        RegisterUserRequest req = new RegisterUserRequest();
        req.setEmail("not-an-email");

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validator.validate(req);

        // we expect violations on fullName, email, username, password
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("fullName", "email", "username", "password");
    }
}
