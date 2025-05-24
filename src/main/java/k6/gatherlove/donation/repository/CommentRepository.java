package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByCampaignId(String campaignId);
}
