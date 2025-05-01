package k6.gatherlove.donation.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
public class Donation {
    private final String id;
    private final String userId;
    private final double amount;
    private final String campaignId;

    @Setter
    private boolean canceled;

    @Setter
    private boolean confirmed;

    public Donation(String userId, double amount, String campaignId) {
        this.id         = UUID.randomUUID().toString();
        this.userId     = userId;
        this.amount     = amount;
        this.campaignId = campaignId;
        this.canceled   = false;
        this.confirmed  = false;
    }
}
