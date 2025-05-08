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
        assertThatThrownBy(() ->
                adminReportService.createReport("camp001", "user1", "spam", null)
        ).isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Admins are not allowed to create reports.");
    }

    @Test
    void testViewReports_returnsAllReports() {
        Report report1 = Report.builder().id(1L).campaignId("camp1").reportedBy("user1").createdAt(LocalDateTime.now()).build();
        Report report2 = Report.builder().id(2L).campaignId("camp2").reportedBy("user2").createdAt(LocalDateTime.now()).build();

        when(reportRepository.findAll()).thenReturn(List.of(report1, report2));

        List<Report> reports = adminReportService.viewReports("ignored");

        assertThat(reports).containsExactly(report1, report2);
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void testDeleteReport_deletesById() {
        Long reportId = 10L;

        adminReportService.deleteReport(reportId);

        verify(reportRepository, times(1)).deleteById(reportId);
    }

    @Test
    void testVerifyCampaign_marksReportsAsVerified() {
        Report report1 = Report.builder().id(1L).campaignId("target").verified(false).build();
        Report report2 = Report.builder().id(2L).campaignId("target").verified(false).build();
        Report report3 = Report.builder().id(3L).campaignId("other").verified(false).build();

        when(reportRepository.findAll()).thenReturn(List.of(report1, report2, report3));

        adminReportService.verifyCampaign("target");

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, times(2)).save(captor.capture());

        List<Report> savedReports = captor.getAllValues();
        assertThat(savedReports).allMatch(Report::isVerified);
        assertThat(savedReports).extracting(Report::getCampaignId).containsOnly("target");
    }
}
