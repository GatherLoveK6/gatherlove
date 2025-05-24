package k6.gatherlove.donation.service;

import k6.gatherlove.donation.client.WalletClient;
import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {
    public static final double INITIAL_USER_BALANCE = 100.0;
    private double userBalance = INITIAL_USER_BALANCE;

    private final DonationRepository repo;
    private final WalletClient walletClient;
    private final CampaignClient campaignClient;

    public DonationServiceImpl(
            DonationRepository repo,
            WalletClient walletClient,
            CampaignClient campaignClient
    ) {
        this.repo           = repo;
        this.walletClient   = walletClient;
        this.campaignClient = campaignClient;
    }

    @Override
    @Transactional
    public Donation createDonation(String userId, double amount, String campaignId) {
        walletClient.deduct(userId, amount);

        if (amount > userBalance) {
            throw new IllegalStateException("Insufficient balance to create donation!");
        }
        userBalance -= amount;

        Donation donation = new Donation(userId, amount, campaignId);
        donation.setConfirmed(true);
        Donation saved = repo.save(donation);

        campaignClient.recordDonation(campaignId, saved.getId(), amount);

        return saved;
    }

    @Override
    @Transactional
    public void cancelDonation(String donationId) {
        Donation donation = repo.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation does not exist!"));

        if (!donation.isCanceled()) {
            campaignClient.removeDonation(donation.getCampaignId(), donationId);

            walletClient.refund(donation.getUserId(), donation.getAmount());

            donation.setCanceled(true);
            repo.save(donation);
            userBalance += donation.getAmount();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Donation findDonationById(String donationId) {
        return repo.findById(donationId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> listAllDonations() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> listDonationsByUser(String userId) {
        return repo.findByUserId(userId);
    }

    @Override
    public double getUserBalance() {
        return userBalance;
    }
}
