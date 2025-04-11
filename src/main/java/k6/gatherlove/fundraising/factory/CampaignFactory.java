package k6.gatherlove.fundraising.factory;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CampaignFactory {

    public static Campaign createCampaign(CampaignCreationRequest request, Long userId) {
        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .goalAmount(request.getGoalAmount())
                .deadline(request.getDeadline())
                .status(CampaignStatus.PENDING_VERIFICATION)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .currentAmount(BigDecimal.ZERO)
                .build();
                
        return campaign;
    }
}
