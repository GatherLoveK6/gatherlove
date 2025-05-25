package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.service.AdminCampaignService;
import k6.gatherlove.AdminDashboard.dto.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminCampaignControllerTest {

    @Mock
    private AdminCampaignService adminCampaignService;

    @InjectMocks
    private AdminCampaignController adminCampaignController;

    private MockMvc mockMvc;
    private Campaign sampleCampaign;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCampaignController).build();
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
