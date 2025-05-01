package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public UserReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(String campaignId, String userId, String reason, String evidenceUrl) {
        // Create a new report from user input
        Report report = Report.builder()
                .campaignId(campaignId)
                .reportedBy(userId)
                .reason(reason)
                .evidenceUrl(evidenceUrl)
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();
        // Here you could also add logic to notify the Admin, if desired.
        return reportRepository.save(report);
    }

    @Override
    public List<Report> viewReports(String userId) {
        // Users can see only their own reports
        return reportRepository.findByReportedBy(userId);
    }

    @Override
    public void deleteReport(Long reportId) {
        // Users are not allowed to delete reports
        throw new UnsupportedOperationException("Users are not allowed to delete reports.");
    }

    @Override
    public void verifyCampaign(String campaignId) {
        // Users are not allowed to verify campaigns
        throw new UnsupportedOperationException("Users are not allowed to verify campaigns.");
    }


}