package k6.gatherlove.report.repository;

import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.repository.ReportRepository;
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
    @DisplayName("Should return reports by reportedBy user ID")
    void testFindByReportedBy() {
        // Arrange
        Report report1 = Report.builder()
                .campaignId("camp1")
                .reportedBy("user123")
                .title("Title 1")
                .description("Description 1")
                .violationType("SPAM")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report2 = Report.builder()
                .campaignId("camp2")
                .reportedBy("user123")
                .title("Title 2")
                .description("Description 2")
                .violationType("FRAUD")
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        Report report3 = Report.builder()
                .campaignId("camp3")
                .reportedBy("otherUser")
                .title("Title 3")
                .description("Description 3")
                .violationType("SCAM")
                .createdAt(LocalDateTime.now())
                .verified(true)
                .build();

        reportRepository.saveAll(List.of(report1, report2, report3));

        // Act
        List<Report> foundReports = reportRepository.findByReportedBy("user123");

        // Assert
        assertThat(foundReports).hasSize(2);
        assertThat(foundReports)
                .extracting(Report::getCampaignId)
                .containsExactlyInAnyOrder("camp1", "camp2");
    }
}
