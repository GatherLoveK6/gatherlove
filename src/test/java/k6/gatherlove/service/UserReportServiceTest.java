package k6.gatherlove.service;

import k6.gatherlove.model.Report;
import k6.gatherlove.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RTest {

    ReportRepository rr;
    UserReportServiceImpl s;

    @BeforeEach
    void abc() {
        rr = mock(ReportRepository.class);
        s = new UserReportServiceImpl(rr);
    }

    @Test
    void A() {
        Report r = Report.builder()
                .id(99L)
                .campaignId("c1")
                .reportedBy("u1")
                .reason("bad stuff")
                .evidenceUrl("http://link")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        when(rr.save(any())).thenReturn(r);

        var result = s.createReport("c1", "u1", "bad stuff", "http://link");
        assertEquals("bad stuff", result.getReason());
        verify(rr, times(1)).save(any());
    }

    @Test
    void B() {
        Report x = Report.builder().id(1L).reportedBy("someone").build();
        Report y = Report.builder().id(2L).reportedBy("someone").build();

        when(rr.findByReportedBy("someone")).thenReturn(Arrays.asList(x, y));

        var z = s.viewReports("someone");
        assertEquals(2, z.size());
    }

    @Test
    void C() {
        Exception e = assertThrows(UnsupportedOperationException.class, () -> {
            s.deleteReport(5L);
        });
        assertTrue(e.getMessage().contains("not allowed"));
    }

    @Test
    void D() {
        var e = assertThrows(UnsupportedOperationException.class, () -> {
            s.verifyCampaign("xxx");
        });
        assertTrue(e.getMessage().contains("verify"));
    }
}
