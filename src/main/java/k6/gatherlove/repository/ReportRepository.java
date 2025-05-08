package k6.gatherlove.repository;

import k6.gatherlove.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // Add custom query methods
}
