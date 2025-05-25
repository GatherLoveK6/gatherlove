package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.TransparencyReportResponse;
import k6.gatherlove.AdminDashboard.service.AdminTransparencyReportService;
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
public class AdminTransparencyReportControllerTest {

    @Mock
    private AdminTransparencyReportService adminTransparencyReportService;

    @InjectMocks
    private AdminTransparencyReportController adminTransparencyReportController;

    private MockMvc mockMvc;
    private TransparencyReportResponse mockReport;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminTransparencyReportController).build();
        mockReport = new TransparencyReportResponse(
                1L,
                100000,
                85000,
                List.of("Books", "School Supplies", "Logistics")
        );
    }

    @Test
    void testGetCampaignReport() throws Exception {
        when(adminTransparencyReportService.getReportByCampaignId(1L)).thenReturn(mockReport);

        mockMvc.perform(get("/admin/campaigns/1/report")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceived").value(100000))
                .andExpect(jsonPath("$.totalUsed").value(85000))
                .andExpect(jsonPath("$.usageBreakdown[0]").value("Books"));
    }
}
