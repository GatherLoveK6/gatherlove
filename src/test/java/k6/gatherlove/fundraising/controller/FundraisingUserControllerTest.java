package k6.gatherlove.fundraising.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use MockitoExtension instead of Spring context to avoid bean conflicts
@ExtendWith(MockitoExtension.class)
class FundraisingUserControllerTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;
    
    @InjectMocks
    private FundraisingUserController fundraisingUserController;
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Create MockMvc manually with standalone setup (no Spring context)
        mockMvc = MockMvcBuilders
                .standaloneSetup(fundraisingUserController)
                .build();
                
        // Set up the security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    
    @AfterEach
    void tearDown() {
        // Clear the SecurityContext to prevent test contamination
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/fundraising/user/info - Unauthenticated User")
    void getCurrentUserInfo_WithUnauthenticatedUser_ReturnsUnauthorized() throws Exception {
        // Set up authentication mock for unauthenticated user
        when(authentication.isAuthenticated()).thenReturn(false);

        // Perform the request and verify
        mockMvc.perform(get("/api/fundraising/user/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authenticated"));
    }

    @Test
    @DisplayName("GET /api/fundraising/user/info - Anonymous User")
    void getCurrentUserInfo_WithAnonymousUser_ReturnsUnauthorized() throws Exception {
        // Set up authentication mock for anonymous user
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Perform the request and verify
        mockMvc.perform(get("/api/fundraising/user/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authenticated"));
    }

    @Test
    @DisplayName("GET /api/fundraising/user/authenticated - Authenticated User")
    void isAuthenticated_WithAuthenticatedUser_ReturnsTrue() throws Exception {
        // Set up authentication mock
        when(authentication.getName()).thenReturn("1234");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");

        // Perform the request and verify
        mockMvc.perform(get("/api/fundraising/user/authenticated")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userId").value("1234"));
    }

    @Test
    @DisplayName("GET /api/fundraising/user/authenticated - Unauthenticated User")
    void isAuthenticated_WithUnauthenticatedUser_ReturnsFalse() throws Exception {
        // Set up authentication mock for unauthenticated user
        when(authentication.isAuthenticated()).thenReturn(false);

        // Perform the request and verify
        mockMvc.perform(get("/api/fundraising/user/authenticated")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    @DisplayName("GET /api/fundraising/user/is-admin - Unauthenticated User")
    void isAdmin_WithUnauthenticatedUser_ReturnsFalse() throws Exception {
        // Set up authentication mock for unauthenticated user
        when(authentication.isAuthenticated()).thenReturn(false);

        // Perform the request and verify
        mockMvc.perform(get("/api/fundraising/user/is-admin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdmin").value(false));
    }
}
