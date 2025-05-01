package k6.gatherlove.donation.comment.repository;

import k6.gatherlove.donation.comment.model.Comment;
import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByCampaignId(String campaignId);
}
