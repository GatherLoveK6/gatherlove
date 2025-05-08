package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import org.springframework.stereotype.Service;

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
    public Donation createDonation(String userId, double amount, String campaignId) {
        checkFunds(amount);
        userBalance -= amount;
        Donation donation = new Donation(userId, amount, campaignId);
        donation.setConfirmed(true);
        donationRepository.save(donation);
        return donation;
    }

    @Override
    public void cancelDonation(String donationId) {
        Donation d = donationRepository.findById(donationId);
        if (d == null) throw new IllegalArgumentException("Donation does not exist!");
        if (d.isCanceled()) return;
        d.setCanceled(true);
        userBalance += d.getAmount();
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
    public List<Donation> listDonationsByUser(String userId) {   // ← new
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
