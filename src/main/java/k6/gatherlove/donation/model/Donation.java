package k6.gatherlove.donation.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


import java.util.UUID;


@Entity
@Table(name = "donations")
@Getter
public class Donation {


    @Id
    @Column(length = 36)
    private String id;


    @Column(nullable = false)
    private String userId;


    @Column(nullable = false)
    private double amount;


    @Column(nullable = false)
    private String campaignId;


    @Setter @Column(nullable = false)
    private boolean canceled;


    @Setter @Column(nullable = false)
    private boolean confirmed;


    protected Donation() {}


    public Donation(String userId, double amount, String campaignId) {
        this.id         = UUID.randomUUID().toString();
        this.userId     = userId;
        this.amount     = amount;
        this.campaignId = campaignId;
        this.canceled   = false;
        this.confirmed  = false;
    }
}
