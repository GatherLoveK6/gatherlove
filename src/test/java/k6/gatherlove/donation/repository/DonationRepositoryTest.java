package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DonationRepositoryTest {

    @Autowired
    private DonationRepository repository;

    @Test
    void testSaveAndFindById() {
        Donation donation = new Donation("testUser", 25.0, "campaignTest");
        Donation saved = repository.save(donation);

        Optional<Donation> found = repository.findById(saved.getId());
        assertTrue(found.isPresent(), "Donation should be found after saving");
        assertEquals(saved.getId(), found.get().getId(), "The IDs must match");
        assertEquals("testUser", found.get().getUserId());
    }

    @Test
    void testFindAll() {
        Donation d1 = new Donation("userA", 10.0, "campaign1");
        Donation d2 = new Donation("userB", 20.0, "campaign2");

        repository.save(d1);
        repository.save(d2);

        List<Donation> all = repository.findAll();
        assertEquals(2, all.size(), "Repository should return 2 donations");
    }
}
