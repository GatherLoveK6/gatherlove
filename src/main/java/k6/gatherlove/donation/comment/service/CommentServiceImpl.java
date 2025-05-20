package k6.gatherlove.donation.comment.service;

import k6.gatherlove.donation.comment.model.Comment;
import k6.gatherlove.donation.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repo;
    private final RestTemplate rest;

    // defaults to empty string if not defined
    @Value("${notification.service.url:}")
    private String notificationBaseUrl;

    public CommentServiceImpl(CommentRepository repo,
                              RestTemplateBuilder builder) {
        this.repo = repo;
        this.rest = builder.build();
    }

    @Override
    public Comment addComment(String campaignId, String userId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        Comment saved = repo.save(new Comment(campaignId, userId, text));

        // notify fundraiser via REST call, only if we have a URL
        if (notificationBaseUrl != null && !notificationBaseUrl.isBlank()) {
            try {
                rest.postForEntity(
                        notificationBaseUrl + "/notifications/comments",
                        Map.of(
                                "campaignId", campaignId,
                                "commentId",   saved.getId(),
                                "userId",      userId,
                                "text",        text
                        ),
                        Void.class
                );
            } catch (RestClientException ex) {
                System.err.println("Warning: failed to notify on new comment: " + ex.getMessage());
            }
        }

        return saved;
    }

    @Override
    public List<Comment> listComments(String campaignId) {
        return repo.findByCampaignId(campaignId);
    }
}
