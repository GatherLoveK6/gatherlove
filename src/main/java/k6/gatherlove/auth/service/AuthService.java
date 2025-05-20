package k6.gatherlove.auth.service;

import k6.gatherlove.auth.model.User;

public interface AuthService {
    User register(String username, String password);
    String login(String username, String password);
}

// tes