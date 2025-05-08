package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import k6.gatherlove.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public AdminReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(String campaignId, String userId, String reason, String evidenceUrl) {
        // Admins do not create reports. Only users report campaigns.
        throw new UnsupportedOperationException("Admins are not allowed to create reports.");
    }

    @Override
    public List<Report> viewReports(String userId) {
        // Admins can view all reports regardless of the reporter
        return reportRepository.findAll();
    }

    @Override
    public void deleteReport(Long reportId) {
        // Admins can delete a report if it is found invalid
        reportRepository.deleteById(reportId);
    }

    @Override
    public void verifyCampaign(String campaignId) {
        // For simplicity, verify campaigns by marking all reports for the given campaign as verified.
        List<Report> reports = reportRepository.findAll();
        reports.stream()
                .filter(report -> report.getCampaignId().equals(campaignId))
                .forEach(report -> {
                    report.setVerified(true);
                    reportRepository.save(report);
                });
    }


}
