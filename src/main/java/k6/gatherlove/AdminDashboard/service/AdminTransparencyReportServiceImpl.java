package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.TransparencyReportResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminTransparencyReportServiceImpl implements AdminTransparencyReportService {

    @Override
    public TransparencyReportResponse getReportByCampaignId(Long campaignId) {
        return generateMockReport(campaignId);
    }

    private TransparencyReportResponse generateMockReport(Long campaignId) {
        return new TransparencyReportResponse(
                campaignId, 100000, 85000,
                List.of("Books", "School Supplies", "Logistics")
        );
    }
}