package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminAnnouncementServiceImplTest {

    private final AdminAnnouncementServiceImpl service = new AdminAnnouncementServiceImpl();

    @Test
    void testSendAnnouncement() {
        AnnouncementRequest request = new AnnouncementRequest("Test Title", "This is a test message.");
        assertDoesNotThrow(() -> service.sendAnnouncement(request));
    }
}