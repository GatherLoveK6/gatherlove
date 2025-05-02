package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminStatisticsServiceImplTest {

    private final AdminStatisticsServiceImpl service = new AdminStatisticsServiceImpl();

    @Test
    void testGetPlatformStatistics() {
        AdminStatisticsResponse response = service.getPlatformStatistics();

        assertNotNull(response);
        assertEquals(300, response.getTotalUsers());
        assertEquals(500, response.getTotalDonations());
        assertEquals(100, response.getTotalCampaigns());
    }
}
