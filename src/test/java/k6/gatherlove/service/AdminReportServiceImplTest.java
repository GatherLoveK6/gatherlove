package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminReportServiceImplTest {

    private ReportRepository reportRepository;
    private AdminReportServiceImpl adminReportService;

    @BeforeEach
    void setup() {
        reportRepository = mock(ReportRepository.class);
        adminReportService = new AdminReportServiceImpl(reportRepository);
    }

    @Test
    void testCreateReport_throwsException() {
        // Arrange

        // Act & Assert
    }

    @Test
    void testViewReports_returnsAllReports() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testDeleteReport_deletesById() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testVerifyCampaign_marksReportsAsVerified() {
        // Arrange

        // Act

        // Assert
    }
}
