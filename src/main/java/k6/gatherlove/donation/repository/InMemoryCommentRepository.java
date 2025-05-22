package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Comment;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
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
                .sorted(Comparator.comparing(Comment::getTimestamp))
                .collect(Collectors.toUnmodifiableList());
    }
}
