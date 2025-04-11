package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import java.util.List;

public interface DonationService {
    Donation createDonation(double amount, String campaignId);
    void cancelDonation(String donationId);
    Donation findDonationById(String donationId);
    List<Donation> listAllDonations();
    double getUserBalance();
}
