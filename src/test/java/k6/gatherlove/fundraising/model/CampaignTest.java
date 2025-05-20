package k6.gatherlove.fundraising.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class CampaignTest {

    @Test
    void builderAndGettersSettersWork() {
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = Campaign.builder()
                .id(1L)
                .title("Test Campaign")
                .description("A test campaign description")
                .goalAmount(new BigDecimal("1000.00"))
                .currentAmount(new BigDecimal("100.00"))
                .deadline(LocalDate.now().plusDays(10))
                .createdAt(now)
                .updatedAt(now)
                .userId(2L)
                .proofFilePath("proofs/file.pdf")
                .status(CampaignStatus.PENDING_VERIFICATION)
                .build();

        assertThat(campaign.getId()).isEqualTo(1L);
        assertThat(campaign.getTitle()).isEqualTo("Test Campaign");
        assertThat(campaign.getDescription()).isEqualTo("A test campaign description");
        assertThat(campaign.getGoalAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(campaign.getCurrentAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(campaign.getDeadline()).isAfterOrEqualTo(LocalDate.now());
        assertThat(campaign.getCreatedAt()).isEqualTo(now);
        assertThat(campaign.getUpdatedAt()).isEqualTo(now);
        assertThat(campaign.getUserId()).isEqualTo(2L);
        assertThat(campaign.getProofFilePath()).isEqualTo("proofs/file.pdf");
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);

        // Test setters
        campaign.setTitle("Updated Title");
        assertThat(campaign.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void prePersistSetsTimestampsAndCurrentAmount() {
        Campaign campaign = new Campaign();
        campaign.setCurrentAmount(null);
        campaign.onCreate();

        assertThat(campaign.getCreatedAt()).isNotNull();
        assertThat(campaign.getUpdatedAt()).isNotNull();
        assertThat(campaign.getCurrentAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void preUpdateUpdatesUpdatedAt() throws InterruptedException {
        Campaign campaign = new Campaign();
        campaign.onCreate();
        LocalDateTime beforeUpdate = campaign.getUpdatedAt();
        Thread.sleep(5); // ensure time difference
        campaign.onUpdate();
        assertThat(campaign.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    void equalsHashCodeToStringWork() {
        Campaign c1 = Campaign.builder()
                .id(1L)
                .title("A")
                .build();
        Campaign c2 = Campaign.builder()
                .id(1L)
                .title("A")
                .build();
        Campaign c3 = Campaign.builder()
                .id(2L)
                .title("B")
                .build();

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).hasSameHashCodeAs(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1.toString()).contains("Campaign");
    }
}