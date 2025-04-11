package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleManagementService {

    private final ArticleManagementRepository articleRepository;

    public ArticleManagementModel createArticle(ArticleManagementModel article) {
        return articleRepository.save(article);
    }

    public ArticleManagementModel getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public List<ArticleManagementModel> getAllArticles() {
        return articleRepository.findAll();
    }

    public ArticleManagementModel updateArticle(Long id, ArticleManagementModel updatedArticle) {
        ArticleManagementModel existing = getArticleById(id);
        existing.setTitle(updatedArticle.getTitle());
        existing.setContent(updatedArticle.getContent());
        return articleRepository.save(existing);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
