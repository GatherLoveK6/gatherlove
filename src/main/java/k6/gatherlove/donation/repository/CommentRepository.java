package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Comment;
import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByCampaignId(String campaignId);
}
