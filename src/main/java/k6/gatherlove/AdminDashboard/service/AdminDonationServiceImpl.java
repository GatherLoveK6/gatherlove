package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDonationServiceImpl implements AdminDonationService {

    private final DonationRepository donationRepository;

    private List<DonationHistoryResponse> mockDonations = null;

    @SuppressWarnings("unused")
    public void setMockDonations(List<DonationHistoryResponse> mockDonations) {
        this.mockDonations = mockDonations;
    }

    @Override
    public List<DonationHistoryResponse> getDonationHistoryByCampaignId(String campaignId) {
        return donationRepository.findByCampaignId(campaignId).stream()
                .map((Donation d) -> new DonationHistoryResponse(
                        d.getUserId(),
                        d.getAmount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DonationHistoryResponse> getAllDonations() {
        if (mockDonations != null) {
            return mockDonations;
        }
        return generateMockDonations();
    }

    private List<DonationHistoryResponse> generateMockDonations() {
        return List.of(
                new DonationHistoryResponse("Jane", 50000),
                new DonationHistoryResponse("John", 75000)
        );
    }
}
