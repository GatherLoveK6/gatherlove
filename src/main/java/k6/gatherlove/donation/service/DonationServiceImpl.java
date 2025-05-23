package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {

    public static final double INITIAL_USER_BALANCE = 100.0;

    private double userBalance = INITIAL_USER_BALANCE;

    private final DonationRepository donationRepository;

    public DonationServiceImpl(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    @Transactional
    public Donation createDonation(String userId, double amount, String campaignId) {
        checkFunds(amount);
        userBalance -= amount;

        Donation donation = new Donation(userId, amount, campaignId);
        donation.setConfirmed(true);

        // save() returns the managed entity (with ID populated)
        return donationRepository.save(donation);
    }

    @Override
    @Transactional
    public void cancelDonation(String donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation does not exist!"));

        if (!donation.isCanceled()) {
            donation.setCanceled(true);
            donationRepository.save(donation);
            userBalance += donation.getAmount();
        }
    }

    @Override
    public Donation findDonationById(String donationId) {
        return donationRepository.findById(donationId)
                .orElse(null);
    }

    @Override
    public List<Donation> listAllDonations() {
        return donationRepository.findAll();
    }

    @Override
    public List<Donation> listDonationsByUser(String userId) {
        return donationRepository.findByUserId(userId);
    }

    @Override
    public double getUserBalance() {
        return userBalance;
    }

    private void checkFunds(double amount) {
        if (amount > userBalance) {
            throw new IllegalStateException("Insufficient balance to create donation!");
        }
    }
}
