package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import java.util.List;

public interface AdminDonationService {
    List<DonationHistoryResponse> getAllDonations();
    List<DonationHistoryResponse> getDonationHistoryByCampaignId(String campaignId);
}
