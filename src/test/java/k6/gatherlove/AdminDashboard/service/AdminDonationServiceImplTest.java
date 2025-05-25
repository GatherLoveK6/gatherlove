package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminDonationServiceImplTest {

    private final AdminDonationServiceImpl service = new AdminDonationServiceImpl(null); // null for donationRepository since we're testing mock

    @Test
    void testGetAllDonations() {
        List<DonationHistoryResponse> donations = service.getAllDonations();

        assertNotNull(donations);
        assertFalse(donations.isEmpty());
        assertEquals(2, donations.size());
        assertEquals("Jane", donations.get(0).getUserId());
        assertEquals(50000, donations.get(0).getAmount());
        assertEquals("John", donations.get(1).getUserId());
        assertEquals(75000, donations.get(1).getAmount());
    }
}
