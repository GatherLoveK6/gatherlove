package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.Campaign;
import k6.gatherlove.AdminDashboard.service.AdminCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminCampaignStatusControllerTest {

    @Mock
    private AdminCampaignService adminCampaignService;

    @InjectMocks
    private AdminCampaignController adminCampaignController;

    private MockMvc mockMvc;
    private Campaign updatedCampaign;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCampaignController).build();
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
