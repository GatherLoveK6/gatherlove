package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import java.util.List;

public interface DonationRepository {
    Donation save(Donation donation);
    Donation findById(String donationId);
    List<Donation> findAll();
}
