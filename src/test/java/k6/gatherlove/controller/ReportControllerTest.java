package k6.gatherlove.controller;

import k6.gatherlove.dto.ReportRequestDTO;
import k6.gatherlove.factory.ReportServiceFactory;
import k6.gatherlove.model.Report;
import k6.gatherlove.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        // TODO: initialize mocks and controller
    }

    @Test
    void testCreateReport_returnsCreatedReport() {
        // TODO: mock request and expected response
        // TODO: call controller and assert result
    }

    @Test
    void testGetReports_adminRole_returnsAllReports() {
        // TODO: mock admin report fetch
        // TODO: call controller and assert result
    }

    @Test
    void testGetReports_userRoleWithUserId_returnsUserReports() {
        // TODO: mock user report fetch
        // TODO: call controller and assert result
    }

    @Test
    void testGetReports_userRoleWithoutUserId_returnsBadRequest() {
        // TODO: test response when userId is missing
    }

    @Test
    void testDeleteReport_delegatesToService() {
        // TODO: verify deleteReport is called correctly
    }

    @Test
    void testVerifyCampaign_callsVerifyCampaignAndReturnsSuccess() {
        // TODO: verify verifyCampaign is called and response is correct
    }
}
