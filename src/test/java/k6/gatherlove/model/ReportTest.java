package k6.gatherlove.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Test
    void testReportBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        Report report = Report.builder()
                .campaignId("CAMP001")
                .reportedBy("USER123")
                .title("Spam Title")
                .description("This campaign contains misleading info.")
                .violationType("SPAM")
                .createdAt(now)
                .verified(false)
                .build();

        assertNull(report.getId()); // Not persisted yet
        assertEquals("CAMP001", report.getCampaignId());
        assertEquals("USER123", report.getReportedBy());
        assertEquals("Spam Title", report.getTitle());
        assertEquals("This campaign contains misleading info.", report.getDescription());
        assertEquals("SPAM", report.getViolationType());
        assertEquals(now, report.getCreatedAt());
        assertFalse(report.isVerified());
    }

    @Test
    void testReportSetters() {
        Report report = new Report();
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();

        report.setId(uuid);
        report.setCampaignId("CAMP002");
        report.setReportedBy("USER456");
        report.setTitle("Fraud Alert");
        report.setDescription("Suspected financial scam.");
        report.setViolationType("FRAUD");
        report.setCreatedAt(now);
        report.setVerified(true);

        assertEquals(uuid, report.getId());
        assertEquals("CAMP002", report.getCampaignId());
        assertEquals("USER456", report.getReportedBy());
        assertEquals("Fraud Alert", report.getTitle());
        assertEquals("Suspected financial scam.", report.getDescription());
        assertEquals("FRAUD", report.getViolationType());
        assertEquals(now, report.getCreatedAt());
        assertTrue(report.isVerified());
    }
}
