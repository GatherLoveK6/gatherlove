package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import k6.gatherlove.AdminDashboard.service.AdminStatisticsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminStatisticsController.class)
public class AdminStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminStatisticsService adminStatisticsService;

    @Test
    void testGetAdminStatistics() throws Exception {
        AdminStatisticsResponse mockResponse = new AdminStatisticsResponse(100, 500, 300);

        when(adminStatisticsService.getPlatformStatistics()).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCampaigns").value(100))
                .andExpect(jsonPath("$.totalDonations").value(500))
                .andExpect(jsonPath("$.totalUsers").value(300));
    }
}
