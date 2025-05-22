package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(String campaignId, String userId, String text);
    List<Comment> listComments(String campaignId);
}
