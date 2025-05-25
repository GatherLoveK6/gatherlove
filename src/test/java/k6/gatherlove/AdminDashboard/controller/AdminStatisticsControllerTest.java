package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import k6.gatherlove.AdminDashboard.service.AdminStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminStatisticsControllerTest {

    @Mock
    private AdminStatisticsService adminStatisticsService;

    @InjectMocks
    private AdminStatisticsController adminStatisticsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminStatisticsController).build();
    }

    @Test
    void testGetAdminStatistics() throws Exception {
        AdminStatisticsResponse mockResponse = new AdminStatisticsResponse(100, 500, 300);

        when(adminStatisticsService.getPlatformStatistics()).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCampaigns").value(100))
                .andExpect(jsonPath("$.totalDonations").value(500))
                .andExpect(jsonPath("$.totalUsers").value(300));
    }
}
