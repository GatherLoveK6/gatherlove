package k6.gatherlove.fundraising.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This is an alternate approach for testing the FundraisingUserController.
 * Use this if you need to test with Spring context.
 */
@SpringBootTest(
    // Include only required components to avoid bean conflicts
    classes = {FundraisingUserController.class}
)
@TestPropertySource(properties = {
    // Disable component scanning to avoid bean conflicts
    "spring.main.banner-mode=off",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class FundraisingUserControllerIntegrationTest {

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    @Test
    public void testIsAuthenticated_WithUnauthenticatedUser() throws Exception {
        // Setup security context with unauthenticated user
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Create controller instance directly
        FundraisingUserController controller = new FundraisingUserController();
        
        // Test the controller method directly without MockMvc
        var response = controller.isAuthenticated();
        var responseBody = response.getBody();
        
        assert responseBody != null;
        assert responseBody instanceof java.util.Map;
        
        @SuppressWarnings("unchecked")
        var map = (java.util.Map<String, Object>) responseBody;
        assert !((Boolean) map.get("authenticated"));
    }
}
