package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.model.CommentArticle;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import k6.gatherlove.ArticleManagement.repository.CommentRepositoryArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImplArticle implements CommentService {

    private final CommentRepositoryArticle commentRepositoryArticle;
    private final ArticleManagementRepository articleRepository;

    @Override
    public CommentArticle addComment(Long articleId, String username, String content) {
        ArticleManagementModel article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        CommentArticle commentArticle = CommentArticle.builder()
                .username(username)
                .content(content)
                .article(article)
                .build();
        return commentRepositoryArticle.save(commentArticle);
    }

    @Override
    public List<CommentArticle> getCommentsForArticle(Long articleId) {
        return commentRepositoryArticle.findByArticleId(articleId);
    }
}