package k6.gatherlove.donation;

import k6.gatherlove.donation.model.Donation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DonationService {

    @Getter
    private double userBalance = 100.0;
    private final List<Donation> donations = new ArrayList<>();

    public Donation createDonation(double amount, String campaignId) {
        if (amount > userBalance) {
            throw new IllegalStateException("Insufficient balance to create donation!");
        }
        userBalance -= amount;
        Donation donation = new Donation(amount, campaignId);
        donation.setConfirmed(true);
        donations.add(donation);
        return donation;
    }

    public void cancelDonation(String donationId) {
        Donation donation = findDonationById(donationId);
        if (donation == null) {
            throw new IllegalArgumentException("Donation does not exist!");
        }
        if (!donation.isCanceled()) {
            donation.setCanceled(true);
            userBalance += donation.getAmount();
        }
    }

    public Donation findDonationById(String donationId) {
        return donations.stream()
                .filter(d -> d.getId().equals(donationId))
                .findFirst()
                .orElse(null);
    }

    public List<Donation> listAllDonations() {
        return new ArrayList<>(donations);
    }
}
