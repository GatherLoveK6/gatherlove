package k6.gatherlove.fundraising.factory;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CampaignFactoryTest {

    @Test
    void createCampaignShouldReturnValidCampaign() {
        CampaignCreationRequest request = new CampaignCreationRequest(
                "Charity Event",
                "A detailed description for charity event.",
                new BigDecimal("1000.00"),
                LocalDate.now().plusDays(10)
        );
        Long userId = 42L;

        Campaign campaign = CampaignFactory.createCampaign(request, userId);

        assertThat(campaign.getTitle()).isEqualTo("Charity Event");
        assertThat(campaign.getDescription()).isEqualTo("A detailed description for charity event.");
        assertThat(campaign.getGoalAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(campaign.getDeadline()).isEqualTo(request.getDeadline());
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);
        assertThat(campaign.getUserId()).isEqualTo(userId);
        assertThat(campaign.getCurrentAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(campaign.getCreatedAt()).isNotNull();
        assertThat(campaign.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateCampaignShouldUpdateFields() {
        Campaign campaign = Campaign.builder()
                .title("Old Title")
                .description("Old Description")
                .goalAmount(new BigDecimal("500.00"))
                .deadline(LocalDate.now().plusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        CampaignUpdateRequest updateRequest = new CampaignUpdateRequest();
        updateRequest.setTitle("New Title");
        updateRequest.setDescription("New Description");
        updateRequest.setGoalAmount(new BigDecimal("1500.00"));
        updateRequest.setDeadline(LocalDate.now().plusDays(20));

        LocalDateTime beforeUpdate = campaign.getUpdatedAt();

        CampaignFactory.updateCampaign(campaign, updateRequest);

        assertThat(campaign.getTitle()).isEqualTo("New Title");
        assertThat(campaign.getDescription()).isEqualTo("New Description");
        assertThat(campaign.getGoalAmount()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(campaign.getDeadline()).isEqualTo(updateRequest.getDeadline());
        assertThat(campaign.getUpdatedAt()).isAfter(beforeUpdate);
    }
}