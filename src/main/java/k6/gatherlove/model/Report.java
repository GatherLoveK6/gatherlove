package k6.gatherlove.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores the campaign identifier that is being reported.
    @Column(name = "campaign_id", nullable = false)
    private String campaignId;

    // Stores the ID of the user who reported the campaign.
    @Column(name = "reported_by", nullable = false)
    private String reportedBy;

    // Reason for the report.
    @Column(nullable = false, length = 500)
    private String reason;

    // URL of the optional evidence.
    @Column(name = "evidence_url", length = 1000)
    private String evidenceUrl;

    // Date and time the report was created.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Flag for whether the report has been verified.
    @Column(nullable = false)
    private boolean verified;
}
