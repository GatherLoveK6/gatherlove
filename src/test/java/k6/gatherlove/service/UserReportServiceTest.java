package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                .id(UUID.randomUUID())
                .campaignId("CAMPAIGN1")
                .reportedBy("USER1")
                .title("Violation Title")
                .description("Violation description")
                .violationType("FRAUD")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(dummyReport);

        Report created = userReportAction.createReport("CAMPAIGN1", "USER1", "Violation Title", "Violation description", "FRAUD");
        assertNotNull(created);
        assertEquals("Violation Title", created.getTitle());
        assertEquals("Violation description", created.getDescription());
        assertEquals("FRAUD", created.getViolationType());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testViewReports() {
        Report report1 = Report.builder()
                .id(UUID.randomUUID())
                .reportedBy("USER1")
                .title("Title1")
                .description("Desc1")
                .violationType("SPAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report2 = Report.builder()
                .id(UUID.randomUUID())
                .reportedBy("USER1")
                .title("Title2")
                .description("Desc2")
                .violationType("FRAUD")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.findByReportedBy("USER1")).thenReturn(Arrays.asList(report1, report2));

        List<Report> reports = userReportAction.viewReports("USER1");
        assertEquals(2, reports.size());
        assertEquals("USER1", reports.get(0).getReportedBy());
    }

    @Test
    void testDeleteReportThrowsException() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            userReportAction.deleteReport(UUID.randomUUID());
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
    void testCreateReportWithNullDescription() {
        Report dummy = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("CAMPX")
                .reportedBy("USR2")
                .title("Missing Description")
                .description(null)
                .violationType("SPAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.save(any())).thenReturn(dummy);

        Report created = userReportAction.createReport("CAMPX", "USR2", "Missing Description", null, "SPAM");
        assertNull(created.getDescription());
    }
}
