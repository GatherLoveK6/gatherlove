package k6.gatherlove.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Test
    void testReportBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        Report report = Report.builder()
                .campaignId("CAMP001")
                .reportedBy("USER123")
                .reason("Inappropriate content")
                .evidenceUrl("https://example.com/image.png")
                .createdAt(now)
                .verified(false)
                .build();

        assertNull(report.getId()); // ID should be null since not persisted yet
        assertEquals("CAMP001", report.getCampaignId());
        assertEquals("USER123", report.getReportedBy());
        assertEquals("Inappropriate content", report.getReason());
        assertEquals("https://example.com/image.png", report.getEvidenceUrl());
        assertEquals(now, report.getCreatedAt());
        assertFalse(report.isVerified());
    }

    @Test
    void testReportSetters() {
        Report report = new Report();
        LocalDateTime now = LocalDateTime.now();

        report.setId(1L);
        report.setCampaignId("CAMP002");
        report.setReportedBy("USER456");
        report.setReason("Spam");
        report.setEvidenceUrl(null);
        report.setCreatedAt(now);
        report.setVerified(true);

        assertEquals(1L, report.getId());
        assertEquals("CAMP002", report.getCampaignId());
        assertEquals("USER456", report.getReportedBy());
        assertEquals("Spam", report.getReason());
        assertNull(report.getEvidenceUrl());
        assertEquals(now, report.getCreatedAt());
        assertTrue(report.isVerified());
    }
}
