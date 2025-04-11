package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DonationRepositoryTest {

    @Test
    void testSaveAndFindById() {
        // ARRANGE: Create the repository (using a stub for now, if desired)
        DonationRepository repository = new InMemoryDonationRepositoryStub();
        Donation donation = new Donation(25.0, "campaignTest");

        // ACT: Save the donation and then try to retrieve it
        repository.save(donation);
        Donation found = repository.findById(donation.getId());

        // ASSERT: Check that the donation is found and the ID matches
        Assertions.assertNotNull(found, "Donation should be found after saving");
        Assertions.assertEquals(donation.getId(), found.getId(), "The IDs must match");
    }

    @Test
    void testFindAll() {
        DonationRepository repository = new InMemoryDonationRepositoryStub();
        Donation donation1 = new Donation(10.0, "campaign1");
        Donation donation2 = new Donation(20.0, "campaign2");

        repository.save(donation1);
        repository.save(donation2);

        // ASSERT: Verify that findAll returns exactly two donations.
        Assertions.assertEquals(2, repository.findAll().size(), "Repository should return 2 donations");
    }
}
