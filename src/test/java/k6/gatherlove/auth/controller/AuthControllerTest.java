package k6.gatherlove.auth.controller;

import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.strategy.AuthStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private AuthStrategy authStrategy;

    @Test
    @DisplayName("GET / should redirect to /auth/login")
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("GET /auth/register should return register view")
    void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @DisplayName("GET /auth/login should return login view")
    void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("POST /auth/register with valid data should redirect to login")
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("fullName", "Test User")
                        .param("email", "test@example.com")
                        .param("phone", "12345678")
                        .param("password", "password123")
                        .param("address", "Street 123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("POST /auth/login with correct credentials should redirect")
    void testLoginSuccess() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        Mockito.when(authStrategy.login("test@example.com", "password123"))
                .thenReturn(mockUser);

        mockMvc.perform(post("/auth/login")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("POST /auth/login with wrong credentials should return to login with error")
    void testLoginFailure() throws Exception {
        Mockito.when(authStrategy.login("wrong@example.com", "badpass"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "wrong@example.com")
                        .param("password", "badpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("error"));
    }
}
