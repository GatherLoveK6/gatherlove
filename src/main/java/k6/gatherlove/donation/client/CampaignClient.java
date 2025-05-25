package k6.gatherlove.donation.client;

public interface CampaignClient {
    void recordDonation(String campaignId, String donationId, double amount);
    void removeDonation(String campaignId, String donationId);
}