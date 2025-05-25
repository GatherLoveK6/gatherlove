package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.TransparencyReportResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AdminTransparencyReportServiceImplTest {

    private final AdminTransparencyReportServiceImpl service = new AdminTransparencyReportServiceImpl();

    @Test
    void testGetReportByCampaignId() {
        Long campaignId = 42L;
        TransparencyReportResponse response = service.getReportByCampaignId(campaignId);

        assertNotNull(response);
        assertEquals(campaignId, response.getCampaignId());
        assertEquals(100000, response.getTotalReceived());
        assertEquals(85000, response.getTotalUsed());
        assertIterableEquals(
                Arrays.asList("Books", "School Supplies", "Logistics"),
                response.getUsageBreakdown()
        );
    }
}
