package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.InMemoryDonationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DonationServiceTest {

    @Test
    void testCreateDonationWithSufficientFunds() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double initialBalance = donationService.getUserBalance();
        double donationAmount = 50.0;

        Donation donation = donationService.createDonation(donationAmount, "campaign01");

        Assertions.assertNotNull(donation.getId(), "Donation ID should not be null.");
        Assertions.assertFalse(donation.isCanceled(), "Donation should not be canceled.");
        Assertions.assertTrue(donation.isConfirmed(), "Donation should be confirmed.");
        Assertions.assertEquals(donationAmount, donation.getAmount(), "Donation amount must match.");
        Assertions.assertEquals(initialBalance - donationAmount, donationService.getUserBalance(),
                "User balance should decrease by donation amount.");
    }

    @Test
    void testCreateDonationWithInsufficientFunds() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double donationAmount = 200.0;

        Assertions.assertThrows(IllegalStateException.class, () -> {
            donationService.createDonation(donationAmount, "campaign02");
        }, "Should throw exception for insufficient funds.");
    }

    @Test
    void testCancelDonationBeforeTransfer() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double donationAmount = 20.0;
        Donation donation = donationService.createDonation(donationAmount, "campaign03");

        donationService.cancelDonation(donation.getId());
        Donation canceledDonation = donationService.findDonationById(donation.getId());

        Assertions.assertTrue(canceledDonation.isCanceled(), "Donation should be canceled.");
        // After canceling, balance should return to initial value (100.0)
        Assertions.assertEquals(100.0, donationService.getUserBalance(),
                "Balance should be restored after cancellation.");
    }
}
