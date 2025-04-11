package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import java.util.List;

public class DonationServiceImpl implements DonationService {

    public static final double INITIAL_USER_BALANCE = 100.0;
    private double userBalance = INITIAL_USER_BALANCE;
    private final DonationRepository donationRepository;

    public DonationServiceImpl(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    public Donation createDonation(double amount, String campaignId) {
        if (amount > userBalance) {
            throw new IllegalStateException("Insufficient balance to create donation!");
        }
        userBalance -= amount;
        Donation donation = new Donation(amount, campaignId);
        donation.setConfirmed(true);
        donationRepository.save(donation);
        return donation;
    }

    @Override
    public void cancelDonation(String donationId) {
        Donation donation = donationRepository.findById(donationId);
        if (donation == null) {
            throw new IllegalArgumentException("Donation does not exist!");
        }
        if (!donation.isCanceled()) {
            donation.setCanceled(true);
            userBalance += donation.getAmount();
        }
    }

    @Override
    public Donation findDonationById(String donationId) {
        return donationRepository.findById(donationId);
    }

    @Override
    public List<Donation> listAllDonations() {
        return donationRepository.findAll();
    }

    @Override
    public double getUserBalance() {
        return userBalance;
    }
}
