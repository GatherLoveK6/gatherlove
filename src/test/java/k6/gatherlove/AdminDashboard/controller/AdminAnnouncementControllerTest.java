package k6.gatherlove.AdminDashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import k6.gatherlove.AdminDashboard.service.AdminAnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAnnouncementController.class)
public class AdminAnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAnnouncementService adminAnnouncementService;

    @Test
    void testPostAnnouncement() throws Exception {
        AnnouncementRequest request = new AnnouncementRequest("System Update", "We’ll be undergoing maintenance at 10 PM.");
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
