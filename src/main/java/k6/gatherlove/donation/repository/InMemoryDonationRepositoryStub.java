package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import java.util.Collections;
import java.util.List;

public class InMemoryDonationRepositoryStub implements DonationRepository {
    @Override
    public Donation save(Donation donation) {
        // stub: do not actually store anything
        return donation;
    }

    @Override
    public Donation findById(String donationId) {
        // stub: always return null to force failure in tests
        return null;
    }

    @Override
    public List<Donation> findAll() {
        return Collections.emptyList();
    }
}
