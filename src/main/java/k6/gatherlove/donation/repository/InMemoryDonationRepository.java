package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryDonationRepository implements DonationRepository {
    private final List<Donation> donations = new ArrayList<>();

    @Override
    public Donation save(Donation donation) {
        donations.add(donation);
        return donation;
    }

    @Override
    public Donation findById(String donationId) {
        return donations.stream()
                .filter(d -> d.getId().equals(donationId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Donation> findAll() {
        return new ArrayList<>(donations);
    }

    @Override
    public List<Donation> findByUserId(String userId) {
        return donations.stream()
                .filter(d -> d.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
