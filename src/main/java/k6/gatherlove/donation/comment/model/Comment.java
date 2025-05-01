package k6.gatherlove.donation.comment.model;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Comment {
    private final String id;
    private final String campaignId;
    private final String userId;
    private final String text;
    private final Instant timestamp;

    public Comment(String campaignId, String userId, String text) {
        this.id         = UUID.randomUUID().toString();
        this.campaignId = campaignId;
        this.userId     = userId;
        this.text       = text;
        this.timestamp  = Instant.now();
    }
}
