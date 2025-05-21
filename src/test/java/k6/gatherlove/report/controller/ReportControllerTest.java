package k6.gatherlove.report.controller;

import k6.gatherlove.report.enums.Role;
import k6.gatherlove.report.dto.ReportRequestDTO;
import k6.gatherlove.report.factory.ReportServiceFactory;
import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

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
        request.setTitle("Title");
        request.setDescription("Description");
        request.setViolationType("spam");

        Report expectedReport = Report.builder()
                .campaignId("camp1")
                .reportedBy("user1")
                .title("Title")
                .description("Description")
                .violationType("spam")
                .build();

        when(reportServiceFactory.getReportAction(Role.USER)).thenReturn(reportService);
        when(reportService.createReport("camp1", "user1", "Title", "Description", "spam")).thenReturn(expectedReport);

        ResponseEntity<Report> response = reportController.createReport(request);

        assertThat(response.getBody()).isEqualTo(expectedReport);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_adminRole_returnsAllReports() {
        Report report = Report.builder().campaignId("camp1").reportedBy("admin").build();
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);
        when(reportService.viewReports(null)).thenReturn(List.of(report));

        ResponseEntity<List<Report>> response = reportController.getReports(Role.ADMIN, null);

        assertThat(response.getBody()).containsExactly(report);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_userRoleWithUserId_returnsUserReports() {
        Report report = Report.builder().campaignId("camp2").reportedBy("user123").build();
        when(reportServiceFactory.getReportAction(Role.USER)).thenReturn(reportService);
        when(reportService.viewReports("user123")).thenReturn(List.of(report));

        ResponseEntity<List<Report>> response = reportController.getReports(Role.USER, "user123");

        assertThat(response.getBody()).containsExactly(report);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetReports_userRoleWithoutUserId_returnsBadRequest() {
        ResponseEntity<List<Report>> response = reportController.getReports(Role.USER, null);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void testDeleteReport_delegatesToService() {
        UUID reportId = UUID.randomUUID();
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);

        ResponseEntity<Void> response = reportController.deleteReport(reportId, Role.ADMIN);

        verify(reportService, times(1)).deleteReport(reportId);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    void testVerifyCampaign_callsVerifyCampaignAndReturnsSuccess() {
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);

        ResponseEntity<String> response = reportController.verifyCampaign("campX", Role.ADMIN);

        verify(reportService).verifyCampaign("campX");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Campaign verified successfully.");
    }
}
