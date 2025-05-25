package k6.gatherlove.donation.service;

import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.client.WalletClient;
import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class DonationServiceTest {

    @Autowired
    private DonationRepository repo;

    private DonationServiceImpl service;

    @BeforeEach
    void setUp() {
        repo.deleteAll();

        WalletClient walletClient = new WalletClient() {
            @Override public void deduct(String userId, double amount) { }
            @Override public void refund(String userId, double amount) { }
        };

        CampaignClient campaignClient = new CampaignClient() {
            @Override public void recordDonation(String campaignId, String donationId, double amount) { }
            @Override public void removeDonation(String campaignId, String donationId) { }
        };

        service = new DonationServiceImpl(repo, walletClient, campaignClient);
    }

    @Test
    void testCreateDonationWithSufficientFunds() {
        double initialBalance = service.getUserBalance();
        double amount = 50.0;

        Donation d = service.createDonation("testUser", amount, "campaign01");

        assertThat(d.getId()).isNotNull();
        assertThat(d.isCanceled()).isFalse();
        assertThat(d.isConfirmed()).isTrue();
        assertThat(d.getAmount()).isEqualTo(amount);
        assertThat(d.getUserId()).isEqualTo("testUser");
        assertThat(service.getUserBalance())
                .isEqualTo(initialBalance - amount);
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
        Donation d = service.createDonation("alice", 20.0, "campaign03");

        service.cancelDonation(d.getId());
        Donation canceled = service.findDonationById(d.getId());

        assertThat(canceled.isCanceled()).isTrue();
        assertThat(service.getUserBalance())
                .isEqualTo(DonationServiceImpl.INITIAL_USER_BALANCE);
    }

    @Test
    void testListDonationsByUser() {
        Donation d1 = service.createDonation("user1", 10.0, "campA");
        service.createDonation("user2", 20.0, "campB");

        List<Donation> history = service.listDonationsByUser("user1");

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getUserId()).isEqualTo("user1");
        assertThat(history.get(0).getId()).isEqualTo(d1.getId());
    }
}
