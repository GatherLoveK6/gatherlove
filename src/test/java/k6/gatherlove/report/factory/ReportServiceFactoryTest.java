package k6.gatherlove.report.factory;

import k6.gatherlove.report.enums.Role;
import k6.gatherlove.report.service.AdminReportServiceImpl;
import k6.gatherlove.report.service.ReportService;
import k6.gatherlove.report.service.UserReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceFactoryTest {

    private UserReportServiceImpl userReportServiceImpl;
    private AdminReportServiceImpl adminReportServiceImpl;
    private ReportServiceFactory factory;

    @BeforeEach
    void setUp() {
        userReportServiceImpl = mock(UserReportServiceImpl.class);
        adminReportServiceImpl = mock(AdminReportServiceImpl.class);
        factory = new ReportServiceFactory(userReportServiceImpl, adminReportServiceImpl);
    }

    @Test
    void testGetReportActionForUser() {
        ReportService result = factory.getReportAction(Role.USER);
        assertEquals(userReportServiceImpl, result);
    }

    @Test
    void testGetReportActionForAdmin() {
        ReportService result = factory.getReportAction(Role.ADMIN);
        assertEquals(adminReportServiceImpl, result);
    }

    @Test
    void testGetReportActionWithNullRole() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getReportAction(null);
        });
        assertTrue(exception.getMessage().contains("Role cannot be null"));
    }
}