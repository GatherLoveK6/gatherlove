package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import java.util.List;

public interface ReportService {
    Report createReport(String campaignId, String userId, String reason, String evidenceUrl);
    List<Report> viewReports(String userId);
    void deleteReport(Long reportId);
    void verifyCampaign(String campaignId);
}

