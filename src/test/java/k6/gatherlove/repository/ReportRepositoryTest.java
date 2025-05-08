package k6.gatherlove.repository;

import k6.gatherlove.model.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Test
    @DisplayName("Should find reports by reportedBy field")
    void testFindByReportedBy() {
        // Arrange
        Report report1 = Report.builder()
                .campaignId("camp123")
                .reportedBy("user1")
                .reason("Spam")
                .evidenceUrl("http://example.com/evidence1")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report2 = Report.builder()
                .campaignId("camp456")
                .reportedBy("user1")
                .reason("Inappropriate Content")
                .evidenceUrl("http://example.com/evidence2")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        reportRepository.save(report1);
        reportRepository.save(report2);

        // Act
        List<Report> result = reportRepository.findByReportedBy("user1");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Report::getReason)
                .containsExactlyInAnyOrder("Spam", "Inappropriate Content");
    }
}
