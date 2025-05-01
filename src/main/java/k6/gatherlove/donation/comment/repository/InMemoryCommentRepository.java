package k6.gatherlove.donation.comment.repository;

import k6.gatherlove.donation.comment.model.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryCommentRepository implements CommentRepository {
    private final List<Comment> comments = new ArrayList<>();

    @Override
    public Comment save(Comment comment) {
        comments.add(comment);
        return comment;
    }

    @Override
    public List<Comment> findByCampaignId(String campaignId) {
        return comments.stream()
                .filter(c -> c.getCampaignId().equals(campaignId))
                .collect(Collectors.toList());
    }
}
