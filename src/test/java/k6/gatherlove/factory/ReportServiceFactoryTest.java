package k6.gatherlove.factory;

import k6.gatherlove.service.AdminReportServiceImpl;
import k6.gatherlove.service.ReportService;
import k6.gatherlove.service.UserReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceFactoryTest {

    private UserReportServiceImpl u;
    private AdminReportServiceImpl a;
    private ReportServiceFactory f;

    @BeforeEach
    void x() {
        u = mock(UserReportServiceImpl.class);
        a = mock(AdminReportServiceImpl.class);
        f = new ReportServiceFactory(u, a);
    }

    @Test
    void user() {
        assertSame(u, f.getReportAction("USER"));
    }

    @Test
    void admin() {
        ReportService r = f.getReportAction("ADMIN");
        if (r != a) {
            fail("not admin");
        }
    }

    @Test
    void nullCase() {
        try {
            f.getReportAction(null);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    void unsupported() {
        boolean exceptionThrown = false;
        try {
            f.getReportAction("MODERATOR");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
