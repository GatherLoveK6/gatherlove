package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.Campaign;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCampaignServiceImpl implements AdminCampaignService {

    @Override
    public List<Campaign> getCampaignsByStatus(String status) {
        return generateMockCampaigns(status);
    }

    @Override
    public Campaign updateCampaignStatus(Long id, String status) {
        return Campaign.builder()
                .id(id)
                .title("Mock Campaign")
                .status(status)
                .build();
    }

    private List<Campaign> generateMockCampaigns(String status) {
        return List.of(
                Campaign.builder().id(1L).title("Mock Campaign 1").status(status).build(),
                Campaign.builder().id(2L).title("Mock Campaign 2").status(status).build()
        );
    }
}