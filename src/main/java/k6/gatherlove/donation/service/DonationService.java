package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import java.util.List;

public interface DonationService {
    Donation createDonation(String userId, double amount, String campaignId);  // signature changed
    void cancelDonation(String donationId);
    Donation findDonationById(String donationId);
    List<Donation> listAllDonations();
    List<Donation> listDonationsByUser(String userId);
    double getUserBalance();
}
