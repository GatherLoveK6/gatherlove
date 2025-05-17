package k6.gatherlove.auth.controller;

import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.strategy.AuthStrategy;
import k6.gatherlove.auth.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)    // ← disable security filters for slice test
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthStrategy authStrategy;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /auth/login → auth/login view")
    void getLogin() throws Exception {
        mvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /auth/register → auth/register view")
    void getRegister() throws Exception {
        mvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @DisplayName("POST /auth/login (success) sets cookie + redirect")
    void postLoginSuccess() throws Exception {
        User fake = new User();
        fake.setUsername("bob");
        given(authStrategy.login("u@e.com", "pw")).willReturn(fake);
        given(jwtUtil.generateToken(fake)).willReturn("tok123");
        given(jwtUtil.getExpirationMs()).willReturn(3600L);

        mvc.perform(post("/auth/login")
                        .param("email", "u@e.com")
                        .param("password", "pw")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hello"))
                .andExpect(flash().attribute("message", "Login successful!"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        containsString("JWT=tok123")));
    }

    @Test
    @DisplayName("POST /auth/login (failure) redirects back with error")
    void postLoginFailure() throws Exception {
        given(authStrategy.login(anyString(), anyString()))
                .willThrow(new RuntimeException("bad cred"));

        mvc.perform(post("/auth/login")
                        .param("email", "u@e.com")
                        .param("password", "pw")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("error", "bad cred"));
    }

    @Test
    @DisplayName("POST /auth/register (valid) calls strategy + redirect")
    void postRegisterValid() throws Exception {
        mvc.perform(post("/auth/register")
                        .param("fullName", "Bob")
                        .param("email", "bob@ex.com")
                        .param("username", "bob")
                        .param("password", "secret")
                        .param("phone", "123")
                        .param("address", "addr")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("message",
                        "Registration successful – please log in."));

        ArgumentCaptor<RegisterUserRequest> captor =
                ArgumentCaptor.forClass(RegisterUserRequest.class);
        then(authStrategy).should().register(captor.capture());
        RegisterUserRequest dto = captor.getValue();
        assertThat(dto.getFullName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@ex.com");
        assertThat(dto.getUsername()).isEqualTo("bob");
    }

    @Test
    @DisplayName("POST /auth/register (invalid) redirects back with errors")
    void postRegisterInvalid() throws Exception {
        mvc.perform(post("/auth/register")
                        .param("fullName", "Bob")
                        .param("email", "")        // invalid
                        .param("username", "bob")
                        .param("password", "secret")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attributeExists("errors"));
    }
}
