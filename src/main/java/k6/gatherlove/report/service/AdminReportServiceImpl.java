package k6.gatherlove.report.service;

import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public AdminReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(String campaignId, String userId, String title, String description, String violationType) {

        throw new UnsupportedOperationException("Admins are not allowed to create reports.");
    }

    @Override
    public List<Report> viewReports(String userId) {

        return reportRepository.findAll();
    }

    @Override
    public void deleteReport(UUID reportId) {

        reportRepository.deleteById(reportId);
    }

    @Override
    public void verifyCampaign(String campaignId) {

        List<Report> reports = reportRepository.findAll();
        reports.stream()
                .filter(report -> report.getCampaignId().equals(campaignId))
                .forEach(report -> {
                    report.setVerified(true);
                    reportRepository.save(report);
                });
    }


}
