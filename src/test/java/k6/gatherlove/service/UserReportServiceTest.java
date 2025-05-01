package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import k6.gatherlove.repository.ReportRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        Report dummyReport = Report.builder()
                .id(1L)
                .campaignId("CAMPAIGN1")
                .reportedBy("USER1")
                .reason("Violation of rules")
                .evidenceUrl("http://evidence.com/img.jpg")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(dummyReport);

        Report created = userReportAction.createReport("CAMPAIGN1", "USER1", "Violation of rules", "http://evidence.com/img.jpg");
        assertNotNull(created);
        assertEquals("Violation of rules", created.getReason());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testViewReports() {
        Report report1 = Report.builder().id(1L).reportedBy("USER1").build();
        Report report2 = Report.builder().id(2L).reportedBy("USER1").build();
        when(reportRepository.findByReportedBy("USER1")).thenReturn(Arrays.asList(report1, report2));

        List<Report> reports = userReportAction.viewReports("USER1");
        assertEquals(2, reports.size());
    }

    @Test
    void testDeleteReportThrowsException() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            userReportAction.deleteReport(1L);
        });
        String expectedMessage = "Users are not allowed to delete reports.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testVerifyCampaignThrowsException() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            userReportAction.verifyCampaign("CAMPAIGN1");
        });
        String expectedMessage = "Users are not allowed to verify campaigns.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateReportWithNullReason() {
        Report dummy = Report.builder()
                .id(2L)
                .campaignId("CAMPX")
                .reportedBy("USR2")
                .reason(null)
                .evidenceUrl("http://evidence.com/image.png")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.save(any())).thenReturn(dummy);

        Report created = userReportAction.createReport("CAMPX", "USR2", null, "http://evidence.com/image.png");
        assertNull(created.getReason());
    }

}

