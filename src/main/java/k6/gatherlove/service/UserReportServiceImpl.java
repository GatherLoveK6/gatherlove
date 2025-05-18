package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public UserReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(String campaignId, String userId, String title, String description, String violationType) {
        Report report = Report.builder()
                .campaignId(campaignId)
                .reportedBy(userId)
                .title(title)
                .description(description)
                .violationType(violationType)
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();
        return reportRepository.save(report);
    }

    @Override
    public List<Report> viewReports(String userId) {
        // Users can see only their own reports
        return reportRepository.findByReportedBy(userId);
    }

    @Override
    public void deleteReport(UUID reportId) {
        // Users are not allowed to delete reports
        throw new UnsupportedOperationException("Users are not allowed to delete reports.");
    }

    @Override
    public void verifyCampaign(String campaignId) {
        // Users are not allowed to verify campaigns
        throw new UnsupportedOperationException("Users are not allowed to verify campaigns.");
    }


}