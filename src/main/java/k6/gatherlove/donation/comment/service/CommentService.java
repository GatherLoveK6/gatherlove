package k6.gatherlove.donation.comment.service;

import k6.gatherlove.donation.comment.model.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(String campaignId, String userId, String text);
    List<Comment> listComments(String campaignId);
}
