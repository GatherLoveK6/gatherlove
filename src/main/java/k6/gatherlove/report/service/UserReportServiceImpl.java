package k6.gatherlove.report.service;

import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.repository.ReportRepository;
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

        return reportRepository.findByReportedBy(userId);
    }

    @Override
    public void deleteReport(UUID reportId) {

        throw new UnsupportedOperationException("Users are not allowed to delete reports.");
    }

    @Override
    public void verifyCampaign(String campaignId) {

        throw new UnsupportedOperationException("Users are not allowed to verify campaigns.");
    }


}