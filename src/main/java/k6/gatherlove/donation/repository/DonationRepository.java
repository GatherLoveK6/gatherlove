package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRepository
        extends JpaRepository<Donation, String> {

    List<Donation> findByUserId(String userId);
}
