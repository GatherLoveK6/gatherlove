package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminDonationServiceImplTest {

    private final AdminDonationServiceImpl service = new AdminDonationServiceImpl();

    @Test
    void testGetAllDonations() {
        List<DonationHistoryResponse> donations = service.getAllDonations();

        assertNotNull(donations);
        assertFalse(donations.isEmpty());
        assertEquals(2, donations.size());
        assertEquals("Jane", donations.get(0).getDonorName());
        assertEquals("John", donations.get(1).getDonorName());
    }
}