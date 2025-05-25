package k6.gatherlove.AdminDashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import k6.gatherlove.AdminDashboard.service.AdminAnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminAnnouncementControllerTest {

    @Mock
    private AdminAnnouncementService adminAnnouncementService;

    @InjectMocks
    private AdminAnnouncementController adminAnnouncementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminAnnouncementController).build();
    }

    @Test
    void testPostAnnouncement() throws Exception {
        AnnouncementRequest request = new AnnouncementRequest("System Update", "We'll be undergoing maintenance at 10 PM.");
        doNothing().when(adminAnnouncementService).sendAnnouncement(request);

        mockMvc.perform(post("/admin/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
