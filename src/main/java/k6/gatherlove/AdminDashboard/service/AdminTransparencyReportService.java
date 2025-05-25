package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.TransparencyReportResponse;

public interface AdminTransparencyReportService {
    TransparencyReportResponse getReportByCampaignId(Long campaignId);
}