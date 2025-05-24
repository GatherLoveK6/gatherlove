package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.service.AdminStatisticsServiceImpl;
import k6.gatherlove.donation.repository.DonationRepository;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import k6.gatherlove.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AdminStatisticsServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminStatisticsServiceImpl service;

    @Test
    void testGetPlatformStatistics() {
        when(campaignRepository.count()).thenReturn(10L);
        when(donationRepository.count()).thenReturn(25L);
        when(userRepository.count()).thenReturn(5L);

        var response = service.getPlatformStatistics();

        assertEquals(10L, response.getTotalCampaigns());
        assertEquals(25L, response.getTotalDonations());
        assertEquals(5L, response.getTotalUsers());
    }
}