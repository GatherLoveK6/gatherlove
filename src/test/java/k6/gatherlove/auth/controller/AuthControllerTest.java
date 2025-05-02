package k6.gatherlove.auth.controller;

import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.model.User.Role;
import k6.gatherlove.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    public void testRegister() throws Exception {
        User mockUser = new User(1L, "john", "hashedPass", Role.USER);

        when(authService.register("john", "pass123")).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .param("username", "john")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).register("john", "pass123");
    }

    @Test
    public void testLogin() throws Exception {
        when(authService.login("john", "pass123")).thenReturn("fake-jwt-token-for-john");

        mockMvc.perform(post("/auth/login")
                        .param("username", "john")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token-for-john"));

        verify(authService).login("john", "pass123");
    }
}
