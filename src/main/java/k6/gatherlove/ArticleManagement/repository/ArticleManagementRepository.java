package k6.gatherlove.ArticleManagement.repository;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleManagementRepository extends JpaRepository<ArticleManagementModel, Long> {
}
