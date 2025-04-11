package k6.gatherlove.service;

import k6.gatherlove.model.Report;
// import k6.gatherlove.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminReportServiceImpl implements ReportService {

    @Override
    public Report createReport(String campaignId, String userId, String reason, String evidenceUrl) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Report> viewReports(String userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteReport(Long reportId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void verifyCampaign(String campaignId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
