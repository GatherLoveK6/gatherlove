package k6.gatherlove.controller;

import k6.gatherlove.dto.ReportRequestDTO;
import k6.gatherlove.factory.ReportServiceFactory;
import k6.gatherlove.model.Report;
import k6.gatherlove.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    private ReportServiceFactory reportServiceFactory;
    private ReportService reportService;
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        reportServiceFactory = mock(ReportServiceFactory.class);
        reportService = mock(ReportService.class);
        reportController = new ReportController(reportServiceFactory);
    }

    @Test
    void testCreateReport_returnsCreatedReport() {
        ReportRequestDTO request = new ReportRequestDTO();
        request.setCampaignId("camp1");
        request.setUserId("user1");
        request.setReason("spam");
        request.setEvidenceUrl("evidence.com");
        Report expectedReport = Report.builder()
                .campaignId("camp1")
                .reportedBy("user1")
                .reason("spam")
                .evidenceUrl("evidence.com")
                .build();

        when(reportServiceFactory.getReportAction("USER")).thenReturn(reportService);
        when(reportService.createReport("camp1", "user1", "spam", "evidence.com")).thenReturn(expectedReport);

        ResponseEntity<Report> response = reportController.createReport(request);

        assertThat(response.getBody()).isEqualTo(expectedReport);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_adminRole_returnsAllReports() {
        Report report = Report.builder().campaignId("camp1").reportedBy("admin").build();
        when(reportServiceFactory.getReportAction("ADMIN")).thenReturn(reportService);
        when(reportService.viewReports(null)).thenReturn(List.of(report));

        ResponseEntity<List<Report>> response = reportController.getReports("ADMIN", null);

        assertThat(response.getBody()).containsExactly(report);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_userRoleWithUserId_returnsUserReports() {
        Report report = Report.builder().campaignId("camp2").reportedBy("user123").build();
        when(reportServiceFactory.getReportAction("USER")).thenReturn(reportService);
        when(reportService.viewReports("user123")).thenReturn(List.of(report));

        ResponseEntity<List<Report>> response = reportController.getReports("USER", "user123");

        assertThat(response.getBody()).containsExactly(report);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_userRoleWithoutUserId_returnsBadRequest() {
        ResponseEntity<List<Report>> response = reportController.getReports("USER", null);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void testDeleteReport_delegatesToService() {
        when(reportServiceFactory.getReportAction("ADMIN")).thenReturn(reportService);

        ResponseEntity<Void> response = reportController.deleteReport(1L, "ADMIN");

        verify(reportService, times(1)).deleteReport(1L);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    void testVerifyCampaign_callsVerifyCampaignAndReturnsSuccess() {
        when(reportServiceFactory.getReportAction("ADMIN")).thenReturn(reportService);

        ResponseEntity<String> response = reportController.verifyCampaign("campX", "ADMIN");

        verify(reportService).verifyCampaign("campX");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Campaign verified successfully.");
    }
}
