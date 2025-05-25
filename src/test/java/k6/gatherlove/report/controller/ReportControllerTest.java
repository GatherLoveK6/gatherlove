package k6.gatherlove.report.controller;

import k6.gatherlove.report.dto.ReportRequestDTO;
import k6.gatherlove.report.enums.Role;
import k6.gatherlove.report.factory.ReportServiceFactory;
import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    private ReportServiceFactory reportServiceFactory;
    private ReportService reportService;
    private ReportController reportController;

    @BeforeEach
    public void setUp() {
        reportServiceFactory = mock(ReportServiceFactory.class);
        reportService = mock(ReportService.class);
        reportController = new ReportController(reportServiceFactory);
    }

    private void authenticate(String role, String userId) {
        Authentication auth = new TestingAuthenticationToken(userId, null, role);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testCreateReport_asUser_shouldReturnReport() {
        authenticate("ROLE_USER", "user123");
        ReportRequestDTO request = new ReportRequestDTO();
        request.setCampaignId("camp123");
        request.setTitle("test title");
        request.setDescription("desc");
        request.setViolationType("Spam");

        Report mockReport = new Report();
        when(reportServiceFactory.getReportAction(Role.USER)).thenReturn(reportService);
        when(reportService.createReport(any(), any(), any(), any(), any())).thenReturn(mockReport);

        ResponseEntity<Report> response = reportController.createReport(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReport, response.getBody());
    }

    @Test
    public void testCreateReport_asAdmin_shouldThrow403() {
        authenticate("ROLE_ADMIN", "admin1");

        ReportRequestDTO request = new ReportRequestDTO();
        request.setCampaignId("camp123");
        request.setTitle("test title");
        request.setDescription("desc");
        request.setViolationType("Spam");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportController.createReport(request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }


    @Test
    public void testGetReports_asAdmin_shouldReturnAllReports() {
        authenticate("ROLE_ADMIN", "admin1");
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);
        when(reportService.viewReports(null)).thenReturn(List.of());

        ResponseEntity<List<Report>> response = reportController.getReports();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReports_asUser_shouldReturnOwnReports() {
        authenticate("ROLE_USER", "user123");
        when(reportServiceFactory.getReportAction(Role.USER)).thenReturn(reportService);
        when(reportService.viewReports("user123")).thenReturn(List.of());

        ResponseEntity<List<Report>> response = reportController.getReports();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteReport_asAdmin_shouldSucceed() {
        authenticate("ROLE_ADMIN", "admin1");
        UUID reportId = UUID.randomUUID();
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);

        ResponseEntity<Void> response = reportController.deleteReport(reportId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reportService).deleteReport(reportId);
    }

    @Test
    public void testDeleteReport_asUser_shouldFail403() {
        authenticate("ROLE_USER", "user123");
        UUID reportId = UUID.randomUUID();

        ResponseEntity<Void> response = reportController.deleteReport(reportId);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testVerifyCampaign_asAdmin_shouldSucceed() {
        authenticate("ROLE_ADMIN", "admin1");
        when(reportServiceFactory.getReportAction(Role.ADMIN)).thenReturn(reportService);

        ResponseEntity<String> response = reportController.verifyCampaign("camp123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService).verifyCampaign("camp123");
    }

    @Test
    public void testVerifyCampaign_asUser_shouldFail403() {
        authenticate("ROLE_USER", "user123");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportController.verifyCampaign("camp123"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    public void testCreateReport_withInvalidRole_shouldThrowBadRequest() {
        // Simulate unknown role
        authenticate("ROLE_UNKNOWN", "user123");
        ReportRequestDTO request = new ReportRequestDTO();
        request.setCampaignId("camp123");
        request.setTitle("title");
        request.setDescription("desc");
        request.setViolationType("Spam");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportController.createReport(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    public void testGetReports_withInvalidRole_shouldReturnBadRequest() {
        authenticate("ROLE_INVALID", "user123");
        ResponseEntity<List<Report>> response = reportController.getReports();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteReport_withInvalidRole_shouldReturn403() {
        authenticate("ROLE_NOT_ADMIN", "user123");
        UUID reportId = UUID.randomUUID();
        ResponseEntity<Void> response = reportController.deleteReport(reportId);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testVerifyCampaign_withInvalidRole_shouldReturnBadRequest() {
        authenticate("ROLE_FAKE", "admin1");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportController.verifyCampaign("camp123"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

}
