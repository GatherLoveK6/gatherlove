package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleManagementServiceImpl implements ArticleManagementService {

    private final ArticleManagementRepository articleRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // REST client

    @Override
    public ArticleManagementModel createArticle(ArticleManagementModel article) {
        ArticleManagementModel saved = articleRepository.save(article);

        // High-Level Networking: Notify another service
        try {
            String message = "A new article titled '" + saved.getTitle() + "' has been created.";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"message\": \"" + message + "\"}";
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // Replace with actual target service URL
            String url = "http://localhost:8081/notify";
            restTemplate.postForEntity(url, request, String.class);

        } catch (Exception e) {
            System.err.println("Failed to notify external service: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public ArticleManagementModel getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    @Override
    public List<ArticleManagementModel> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public ArticleManagementModel updateArticle(Long id, ArticleManagementModel updatedArticle) {
        ArticleManagementModel existing = getArticleById(id);
        existing.setTitle(updatedArticle.getTitle());
        existing.setContent(updatedArticle.getContent());
        return articleRepository.save(existing);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
