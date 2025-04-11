package k6.gatherlove.donation;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DonationServiceTest {

    @Test
    void testCreateDonationWithSufficientFunds() {
        DonationService donationService = new DonationService();
        double initialUserBalance = donationService.getUserBalance();
        double donationAmount = 50.0;

        Donation donation = donationService.createDonation(donationAmount, "campaign01");

        Assertions.assertNotNull(donation.getId());
        Assertions.assertFalse(donation.isCanceled());
        Assertions.assertTrue(donation.isConfirmed());
        Assertions.assertEquals(donationAmount, donation.getAmount());

        Assertions.assertEquals(initialUserBalance - donationAmount,
                donationService.getUserBalance());
    }

    @Test
    void testCreateDonationWithInsufficientFunds() {
        DonationService donationService = new DonationService();
        double donationAmount = 200.0;

        Assertions.assertThrows(IllegalStateException.class, () -> {
            donationService.createDonation(donationAmount, "campaign02");
        });
    }

    @Test
    void testCancelDonationBeforeTransfer() {
        DonationService donationService = new DonationService();
        double donationAmount = 20.0;
        Donation donation = donationService.createDonation(donationAmount, "campaign03");

        donationService.cancelDonation(donation.getId());
        Donation canceledDonation = donationService.findDonationById(donation.getId());

        Assertions.assertTrue(canceledDonation.isCanceled());
        Assertions.assertEquals(100.0, donationService.getUserBalance());
    }
}