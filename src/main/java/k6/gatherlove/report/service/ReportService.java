package k6.gatherlove.report.service;

import k6.gatherlove.report.model.Report;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    Report createReport(String campaignId, String userId, String title, String description, String violationType);
    List<Report> viewReports(String userId);
    void deleteReport(UUID reportId);
    void verifyCampaign(String campaignId);
}

