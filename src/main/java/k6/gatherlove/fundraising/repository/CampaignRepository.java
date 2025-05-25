package k6.gatherlove.fundraising.repository;

import k6.gatherlove.fundraising.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByUserId(Long userId);
}
