package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import k6.gatherlove.fundraising.repository.CampaignRepository;
import k6.gatherlove.donation.repository.DonationRepository;
import k6.gatherlove.auth.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final CampaignRepository campaignRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    @Override
    public AdminStatisticsResponse getPlatformStatistics() {
        long totalCampaigns = campaignRepository.count();
        long totalDonations = donationRepository.count();
        long totalUsers = userRepository.count();

        return new AdminStatisticsResponse(totalCampaigns, totalDonations, totalUsers);
    }
}

