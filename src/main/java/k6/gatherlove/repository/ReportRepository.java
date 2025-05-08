package k6.gatherlove.repository;

import k6.gatherlove.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportedBy(String reportedBy);
}
