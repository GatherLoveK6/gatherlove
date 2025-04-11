package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.Campaign;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminCampaignServiceImplTest {

    private final AdminCampaignServiceImpl service = new AdminCampaignServiceImpl();

    @Test
    void testGetCampaignsByStatus() {
        String status = "PENDING";
        List<Campaign> campaigns = service.getCampaignsByStatus(status);

        assertEquals(2, campaigns.size());
        assertEquals("PENDING", campaigns.get(0).getStatus());
        assertEquals("Mock Campaign 1", campaigns.get(0).getTitle());
    }

    @Test
    void testUpdateCampaignStatus() {
        Campaign updated = service.updateCampaignStatus(1L, "VERIFIED");

        assertEquals(1L, updated.getId());
        assertEquals("VERIFIED", updated.getStatus());
        assertEquals("Mock Campaign", updated.getTitle());
    }
}
