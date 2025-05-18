package k6.gatherlove.report.service;

import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.service.AdminReportServiceImpl;
import k6.gatherlove.report.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThatThrownBy(() ->
                adminReportService.createReport("camp001", "user1", "Spam Title", "Spam content", "SPAM")
        ).isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Admins are not allowed to create reports.");
    }

    @Test
    void testViewReports_returnsAllReports() {
        Report report1 = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("camp1")
                .reportedBy("user1")
                .title("t1")
                .description("d1")
                .violationType("SPAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report2 = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("camp2")
                .reportedBy("user2")
                .title("t2")
                .description("d2")
                .violationType("FRAUD")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.findAll()).thenReturn(List.of(report1, report2));

        List<Report> reports = adminReportService.viewReports("ignored");

        assertThat(reports).containsExactly(report1, report2);
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void testDeleteReport_deletesById() {
        UUID reportId = UUID.randomUUID();

        adminReportService.deleteReport(reportId);

        verify(reportRepository, times(1)).deleteById(reportId);
    }

    @Test
    void testVerifyCampaign_marksReportsAsVerified() {
        Report report1 = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("target")
                .title("t1")
                .description("d1")
                .violationType("SPAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report2 = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("target")
                .title("t2")
                .description("d2")
                .violationType("FRAUD")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report3 = Report.builder()
                .id(UUID.randomUUID())
                .campaignId("other")
                .title("t3")
                .description("d3")
                .violationType("SCAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(reportRepository.findAll()).thenReturn(List.of(report1, report2, report3));

        adminReportService.verifyCampaign("target");

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, times(2)).save(captor.capture());

        List<Report> savedReports = captor.getAllValues();

        assertThat(savedReports).allMatch(Report::isVerified);
        assertThat(savedReports).extracting(Report::getCampaignId).containsOnly("target");
    }
}
