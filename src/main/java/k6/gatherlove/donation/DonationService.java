package k6.gatherlove.donation;

import k6.gatherlove.donation.model.Donation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DonationService {

    // constant for the initial user balance
    public static final double INITIAL_USER_BALANCE = 100.0;

    @Getter
    private double userBalance = INITIAL_USER_BALANCE;
    private final List<Donation> donations = new ArrayList<>();

    public Donation createDonation(double amount, String campaignId) {
        validateSufficientFunds(amount);
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
        if (donation.isCanceled()) {
            return;
        }
        donation.setCanceled(true);
        userBalance += donation.getAmount();
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

    private void validateSufficientFunds(double amount) {
        if (amount > userBalance) {
            throw new IllegalStateException("Insufficient balance to create donation!");
        }
    }
}