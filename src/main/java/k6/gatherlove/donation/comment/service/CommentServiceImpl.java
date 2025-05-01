package k6.gatherlove.donation.comment.service;

import k6.gatherlove.donation.comment.model.Comment;
import k6.gatherlove.donation.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repo;

    public CommentServiceImpl(CommentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Comment addComment(String campaignId, String userId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        Comment c = new Comment(campaignId, userId, text);
        return repo.save(c);
    }

    @Override
    public List<Comment> listComments(String campaignId) {
        return repo.findByCampaignId(campaignId);
    }
}
