package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DonationRepositoryTest {

    @Test
    void testSaveAndFindById() {
        // use the proper in-memory repository
        DonationRepository repository = new InMemoryDonationRepository();
        // supply a dummy userId now that Donation requires it
        Donation donation = new Donation("testUser", 25.0, "campaignTest");

        repository.save(donation);
        Donation found = repository.findById(donation.getId());

        Assertions.assertNotNull(found, "Donation should be found after saving");
        Assertions.assertEquals(donation.getId(), found.getId(), "The IDs must match");
    }

    @Test
    void testFindAll() {
        DonationRepository repository = new InMemoryDonationRepository();
        // updated to 3-arg constructor
        Donation donation1 = new Donation("userA", 10.0, "campaign1");
        Donation donation2 = new Donation("userB", 20.0, "campaign2");

        repository.save(donation1);
        repository.save(donation2);

        List<Donation> all = repository.findAll();
        Assertions.assertEquals(2, all.size(), "Repository should return 2 donations");
    }
}
