package k6.gatherlove.donation.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
public class Donation {
    private String id;
    private double amount;
    private String campaignId;
    @Setter
    private boolean canceled;
    @Setter
    private boolean confirmed;

    public Donation(double amount, String campaignId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.campaignId = campaignId;
        this.canceled = false;
        this.confirmed = false;
    }
}
