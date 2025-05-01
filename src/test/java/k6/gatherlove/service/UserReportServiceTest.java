package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserReportServiceTest {

    private ReportRepository reportRepository;
    private UserReportServiceImpl userReportAction;

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        userReportAction = new UserReportServiceImpl(reportRepository);
    }

    @Test
    void testCreateReport() {
        // TODO: Implement test logic for creating a report
    }

    @Test
    void testViewReports() {
        // TODO: Implement test logic for viewing user reports
    }

    @Test
    void testDeleteReportThrowsException() {
        // TODO: Implement test logic to ensure user can't delete a report
    }

    @Test
    void testVerifyCampaignThrowsException() {
        // TODO: Implement test logic to ensure user can't verify a campaign
    }
}
