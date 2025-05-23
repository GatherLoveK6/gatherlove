package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DonationServiceTest {

    @Test
    void testCreateDonationWithSufficientFunds() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double initialBalance = donationService.getUserBalance();
        double donationAmount = 50.0;

        // note the new userId argument
        Donation donation = donationService.createDonation("testUser", donationAmount, "campaign01");

        Assertions.assertNotNull(donation.getId(), "Donation ID should not be null.");
        Assertions.assertFalse(donation.isCanceled(), "Donation should not be canceled.");
        Assertions.assertTrue(donation.isConfirmed(), "Donation should be confirmed.");
        Assertions.assertEquals(donationAmount, donation.getAmount(), "Donation amount must match.");
        Assertions.assertEquals("testUser", donation.getUserId(), "Donation should carry the correct userId.");
        Assertions.assertEquals(
                initialBalance - donationAmount,
                donationService.getUserBalance(),
                "User balance should decrease by donation amount."
        );
    }

    @Test
    void testCreateDonationWithInsufficientFunds() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double donationAmount = 200.0;

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> donationService.createDonation("richPleb", donationAmount, "campaign02"),
                "Should throw exception for insufficient funds."
        );
    }

    @Test
    void testCancelDonationBeforeTransfer() {
        DonationService donationService = new DonationServiceImpl(new InMemoryDonationRepository());
        double donationAmount = 20.0;
        Donation donation = donationService.createDonation("alice", donationAmount, "campaign03");

        donationService.cancelDonation(donation.getId());
        Donation canceledDonation = donationService.findDonationById(donation.getId());

        Assertions.assertTrue(canceledDonation.isCanceled(), "Donation should be canceled.");
        // after canceling, balance should return to initial value (100.0)
        Assertions.assertEquals(
                DonationServiceImpl.INITIAL_USER_BALANCE,
                donationService.getUserBalance(),
                "Balance should be restored after cancellation."
        );
    }

    @Test
    void testListDonationsByUser() {
        DonationService service = new DonationServiceImpl(new InMemoryDonationRepository());

        // user1 gives 10, user2 gives 20
        Donation d1 = service.createDonation("user1", 10.0, "campA");
        service.createDonation("user2", 20.0, "campB");

        // ask for user1’s history
        List<Donation> user1History = service.listDonationsByUser("user1");

        Assertions.assertEquals(1, user1History.size(), "Should only return user1's donations");
        Assertions.assertEquals("user1", user1History.get(0).getUserId(), "Returned donation must belong to user1");
        Assertions.assertEquals(d1.getId(), user1History.get(0).getId(), "Donation ID must match the one created by user1");
    }
}
