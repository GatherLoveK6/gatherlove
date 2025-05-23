package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DonationRepositoryTest {

    @Autowired
    private DonationRepository repo;

    @Test
    void saveAndFindByUserId() {
        Donation d1 = new Donation("userA", 42.0, "camp1");
        repo.save(d1);

        List<Donation> donations = repo.findByUserId("userA");

        assertThat(donations)
                .hasSize(1)
                .first()
                .extracting(Donation::getAmount, Donation::getCampaignId)
                .containsExactly(42.0, "camp1");
    }

    @Test
    void saveAndFindByCampaignId() {
        Donation d2 = new Donation("userB", 13.0, "campX");
        repo.save(d2);

        List<Donation> result = repo.findByCampaignId("campX");

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(Donation::getUserId, Donation::getAmount)
                .containsExactly("userB", 13.0);
    }
}
