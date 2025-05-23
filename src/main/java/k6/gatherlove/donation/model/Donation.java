package k6.gatherlove.donation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String campaignId;

    @Column(nullable = false)
    private double amount;

    @Setter
    @Column(nullable = false)
    private boolean confirmed;

    @Setter
    @Column(nullable = false)
    private boolean canceled;

    protected Donation() {
    }

    public Donation(String userId, double amount, String campaignId) {
        this.id         = UUID.randomUUID().toString();
        this.userId     = userId;
        this.amount     = amount;
        this.campaignId = campaignId;
        this.confirmed  = false;
        this.canceled   = false;
    }


    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isCanceled() {
        return canceled;
    }

}
