package k6.gatherlove.ArticleManagement.repository;

import k6.gatherlove.ArticleManagement.model.CommentArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepositoryArticle extends JpaRepository<CommentArticle, Long> {
    List<CommentArticle> findByArticleId(Long articleId);
}