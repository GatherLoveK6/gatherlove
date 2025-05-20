package k6.gatherlove.donation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DonationRequest {
    private String userId;
    private String campaignId;
    private double amount;
}
