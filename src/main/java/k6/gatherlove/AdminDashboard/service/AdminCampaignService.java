package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.Campaign;

import java.util.List;

public interface AdminCampaignService {
    List<Campaign> getCampaignsByStatus(String status);
}
