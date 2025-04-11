package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.service.AdminCampaignService;
import k6.gatherlove.campaign.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCampaignController.class)
public class AdminCampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCampaignService adminCampaignService;

    private Campaign sampleCampaign;

    @BeforeEach
    void setUp() {
        sampleCampaign = Campaign.builder()
                .id(1L)
                .title("Bantu Sekolah")
                .status("ongoing")
                .build();
    }

    @Test
    void testGetCampaignsByStatus() throws Exception {
        when(adminCampaignService.getCampaignsByStatus("ongoing")).thenReturn(List.of(sampleCampaign));

        mockMvc.perform(get("/admin/campaigns")
                        .param("status", "ongoing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Bantu Sekolah"));
    }
}
