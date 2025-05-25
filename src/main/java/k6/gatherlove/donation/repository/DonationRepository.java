package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DonationRepository extends JpaRepository<Donation, String> {
    List<Donation> findByUserId(String userId);
    List<Donation> findByCampaignId(String campaignId);
}
