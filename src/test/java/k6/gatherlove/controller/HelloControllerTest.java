package k6.gatherlove.controller;

import k6.gatherlove.auth.filter.JwtAuthenticationFilter;
import k6.gatherlove.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters to avoid dependency issues
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the JWT dependencies that would otherwise cause issues
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean 
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("hello"));
    }
}
