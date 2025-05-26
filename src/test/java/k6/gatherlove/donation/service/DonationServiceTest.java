package k6.gatherlove.donation.service;

import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.client.WalletClient;
import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import k6.gatherlove.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private WalletClient walletClient;

    @Mock
    private CampaignClient campaignClient;
    
    @Mock
    private MetricsService metricsService;

    private DonationServiceImpl service;

    @BeforeEach
    void setUp() {
        donationRepository.deleteAll();

        service = new DonationServiceImpl(
                donationRepository,
                walletClient,
                campaignClient,
                metricsService
        );
    }

    @Test
    void testCreateDonationWithSufficientFunds() {
        // Given
        String userId = "user123";
        double amount = 50.0;
        String campaignId = "campaign456";
        
        Donation savedDonation = new Donation(userId, amount, campaignId);
        savedDonation.setConfirmed(true);

        when(donationRepository.save(any(Donation.class))).thenReturn(savedDonation);

        // When
        Donation result = service.createDonation(userId, amount, campaignId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId()); // Don't check specific UUID
        assertTrue(result.isConfirmed());
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(campaignId, result.getCampaignId());

        verify(walletClient).deduct(userId, amount);
        verify(campaignClient).recordDonation(eq(campaignId), any(String.class), eq(amount));
        verify(metricsService).incrementDonationCreated();
    }

    @Test
    void testCreateDonationWithInsufficientFunds() {
        assertThrows(
                IllegalStateException.class,
                () -> service.createDonation("richPleb", 200.0, "campaign02")
        );
    }

    @Test
    void testCancelDonationBeforeTransfer() {
        // Given
        String userId = "user123";
        double amount = 30.0;
        String campaignId = "campaign456";
        
        Donation savedDonation = new Donation(userId, amount, campaignId);
        savedDonation.setConfirmed(true);

        when(donationRepository.save(any(Donation.class))).thenReturn(savedDonation);
        when(donationRepository.findById(any(String.class))).thenReturn(Optional.of(savedDonation));

        // When
        Donation donation = service.createDonation(userId, amount, campaignId);
        service.cancelDonation(donation.getId());

        // Then
        assertTrue(donation.isCanceled());
        verify(campaignClient).removeDonation(eq(campaignId), any(String.class));
        verify(walletClient).refund(userId, amount);
    }

    @Test
    void testListDonationsByUser() {
        // Given
        String userId = "user123";
        double amount = 25.0;
        String campaignId = "campaign456";
        
        Donation savedDonation = new Donation(userId, amount, campaignId);
        savedDonation.setConfirmed(true);

        when(donationRepository.save(any(Donation.class))).thenReturn(savedDonation);
        when(donationRepository.findByUserId(userId)).thenReturn(List.of(savedDonation));

        // When
        service.createDonation(userId, amount, campaignId);
        List<Donation> userDonations = service.listDonationsByUser(userId);

        // Then
        assertFalse(userDonations.isEmpty());
        assertEquals(1, userDonations.size());
        assertEquals(userId, userDonations.get(0).getUserId());
    }

    @Test
    void createDonation_success() {
        String userId = "user1";
        double amount = 50.0;
        String campaignId = "campaign1";

        Donation savedDonation = new Donation(userId, amount, campaignId);
        savedDonation.setConfirmed(true);

        when(donationRepository.save(any(Donation.class))).thenReturn(savedDonation);

        Donation result = service.createDonation(userId, amount, campaignId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(campaignId, result.getCampaignId());
        assertTrue(result.isConfirmed());

        verify(walletClient).deduct(userId, amount);
        verify(campaignClient).recordDonation(campaignId, savedDonation.getId(), amount);
        verify(metricsService).incrementDonationCreated();
        verify(donationRepository).save(any(Donation.class));
    }

    @Test
    void createDonation_insufficientBalance_throws() {
        String userId = "user1";
        double amount = 150.0; // Greater than INITIAL_USER_BALANCE (100.0)
        String campaignId = "campaign1";

        assertThrows(IllegalStateException.class, () ->
                service.createDonation(userId, amount, campaignId));

        verify(walletClient).deduct(userId, amount);
        verify(campaignClient, never()).recordDonation(anyString(), anyString(), anyDouble());
        verify(metricsService, never()).incrementDonationCreated();
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void cancelDonation_success() {
        String donationId = "donation1";
        Donation donation = new Donation("user1", 50.0, "campaign1");
        donation.setConfirmed(true);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        service.cancelDonation(donationId);

        assertTrue(donation.isCanceled());
        verify(campaignClient).removeDonation(donation.getCampaignId(), donationId);
        verify(walletClient).refund(donation.getUserId(), donation.getAmount());
        verify(donationRepository).save(donation);
    }

    @Test
    void findDonationById_exists() {
        String donationId = "donation1";
        Donation donation = new Donation("user1", 50.0, "campaign1");

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        Donation result = service.findDonationById(donationId);

        assertNotNull(result);
        assertEquals(donation, result);
    }

    @Test
    void findDonationById_notExists() {
        String donationId = "nonexistent";

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        Donation result = service.findDonationById(donationId);

        assertNull(result);
    }
}
