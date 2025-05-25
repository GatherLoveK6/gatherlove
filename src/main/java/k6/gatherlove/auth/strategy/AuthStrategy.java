package k6.gatherlove.auth.strategy;

import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.User;

public interface AuthStrategy {
    User login(String email, String password);
    User register(RegisterUserRequest request);
}
