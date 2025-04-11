package k6.gatherlove.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReportTest {

    @Test
    void testSomething() {
        Report r = new Report();
        r.setCampaignId("abc");
        r.setReportedBy("def");
        r.setReason("ghi");
        r.setEvidenceUrl("url");
        r.setCreatedAt(LocalDateTime.now());
        r.setVerified(true);

        if (!r.getCampaignId().equals("abc")) {
            fail("campaignId wrong");
        }
        if (!r.getReportedBy().equals("def")) {
            fail("reportedBy wrong");
        }
        if (!r.getReason().equals("ghi")) {
            fail("reason wrong");
        }
    }

    @Test
    void testBuilder() {
        LocalDateTime t = LocalDateTime.of(2023, 1, 1, 1, 1);
        Report x = Report.builder()
                .campaignId("x")
                .reportedBy("y")
                .reason("z")
                .evidenceUrl("e")
                .createdAt(t)
                .verified(false)
                .build();

        assertEquals("x", x.getCampaignId());
        assertEquals("y", x.getReportedBy());
        assertEquals("z", x.getReason());
        assertEquals("e", x.getEvidenceUrl());
        assertEquals(t, x.getCreatedAt());
        assertFalse(x.isVerified());
    }

    @Test
    void testStuff() {
        Report r = new Report();
        r.setId(10L);
        r.setCampaignId("test");
        r.setReportedBy("u");
        r.setReason("bad");
        r.setCreatedAt(LocalDateTime.now());
        r.setVerified(false);

        assertTrue(r.getId() == 10);
        assertNotNull(r.getCreatedAt());
    }
}
