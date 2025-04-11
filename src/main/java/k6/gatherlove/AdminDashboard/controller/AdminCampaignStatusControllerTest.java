package k6.gatherlove.AdminDashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.AdminDashboard.dto.Campaign;
import k6.gatherlove.AdminDashboard.service.AdminCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCampaignController.class) // you're likely using the same controller
public class AdminCampaignStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCampaignService adminCampaignService;

    private Campaign updatedCampaign;

    @BeforeEach
    void setUp() {
        updatedCampaign = Campaign.builder()
                .id(1L)
                .title("Bantu Sekolah")
                .status("VERIFIED")
                .build();
    }

    @Test
    void testUpdateCampaignStatus() throws Exception {
        when(adminCampaignService.updateCampaignStatus(eq(1L), eq("VERIFIED"))).thenReturn(updatedCampaign);

        mockMvc.perform(patch("/admin/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"VERIFIED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"));
    }
}
