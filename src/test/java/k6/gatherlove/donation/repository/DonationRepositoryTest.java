package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DonationRepositoryTest {

    @Test
    void testSaveAndFindById() {
        // Use the proper in-memory repository
        DonationRepository repository = new InMemoryDonationRepository();
        Donation donation = new Donation(25.0, "campaignTest");

        repository.save(donation);
        Donation found = repository.findById(donation.getId());

        Assertions.assertNotNull(found, "Donation should be found after saving");
        Assertions.assertEquals(donation.getId(), found.getId(), "The IDs must match");
    }

    @Test
    void testFindAll() {
        DonationRepository repository = new InMemoryDonationRepository();
        Donation donation1 = new Donation(10.0, "campaign1");
        Donation donation2 = new Donation(20.0, "campaign2");

        repository.save(donation1);
        repository.save(donation2);

        Assertions.assertEquals(2, repository.findAll().size(), "Repository should return 2 donations");
    }
}
