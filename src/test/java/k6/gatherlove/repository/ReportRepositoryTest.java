package k6.gatherlove.repository;

import k6.gatherlove.model.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Test
    @DisplayName("Should return reports by reportedBy user ID")
    void testFindByReportedBy() {
        // Arrange
        Report report1 = Report.builder()
                .campaignId("camp1")
                .reportedBy("user123")
                .reason("reason 1")
                .createdAt(LocalDateTime.now())
                .build();

        Report report2 = Report.builder()
                .campaignId("camp2")
                .reportedBy("user123")
                .reason("inappropriate")
                .createdAt(LocalDateTime.now())
                .build();

        Report report3 = Report.builder()
                .campaignId("camp3")
                .reportedBy("otherUser")
                .reason("scam")
                .createdAt(LocalDateTime.now())
                .build();

        reportRepository.saveAll(List.of(report1, report2, report3));

        // Act
        List<Report> foundReports = reportRepository.findByReportedBy("user123");

        // Assert
        assertThat(foundReports).hasSize(2);
        assertThat(foundReports).extracting(Report::getCampaignId).containsExactlyInAnyOrder("camp1", "camp2");
    }
}
