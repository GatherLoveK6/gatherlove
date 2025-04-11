package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import java.util.ArrayList;
import java.util.List;

public class InMemoryDonationRepository implements DonationRepository {
    private final List<Donation> donations = new ArrayList<>();

    @Override
    public Donation save(Donation donation) {
        donations.add(donation);
        return donation;
    }

    @Override
    public Donation findById(String donationId) {
        for (Donation d : donations) {
            if (d.getId().equals(donationId)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List<Donation> findAll() {
        return new ArrayList<>(donations);
    }
}
