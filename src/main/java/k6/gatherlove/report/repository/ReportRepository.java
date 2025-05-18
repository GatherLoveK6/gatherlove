package k6.gatherlove.report.repository;

import k6.gatherlove.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByReportedBy(String reportedBy);
}
