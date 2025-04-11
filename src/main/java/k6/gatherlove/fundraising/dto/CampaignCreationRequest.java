package k6.gatherlove.fundraising.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CampaignCreationRequest {
    private String title;
    private String description;
    private double goalAmount;
    private String deadline;
}
