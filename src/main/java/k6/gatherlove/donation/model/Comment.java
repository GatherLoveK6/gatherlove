package k6.gatherlove.donation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;



@Entity
@Table(name = "comments")
@Getter
public class Comment {


    @Id
    @Column(length = 36)
    private String id;


    @Column(nullable = false)
    private String campaignId;


    @Column(nullable = false)
    private String userId;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;


    @Column(nullable = false)
    private Instant timestamp;


    protected Comment() {}


    public Comment(String campaignId, String userId, String text) {
        this.id         = UUID.randomUUID().toString();
        this.campaignId = campaignId;
        this.userId     = userId;
        this.text       = text;
        this.timestamp  = Instant.now();
    }
}
