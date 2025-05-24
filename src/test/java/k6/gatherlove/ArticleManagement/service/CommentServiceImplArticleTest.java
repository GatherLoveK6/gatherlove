package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.model.CommentArticle;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import k6.gatherlove.ArticleManagement.repository.CommentRepositoryArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentServiceImplArticleTest {

    @Autowired
    private CommentServiceImplArticle commentService;

    @Autowired
    private ArticleManagementRepository articleRepository;

    @Autowired
    private CommentRepositoryArticle commentRepository;

    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();

        article = ArticleManagementModel.builder()
                .title("Sample Article")
                .content("This is a test article")
                .author("Admin")
                .build();

        article = articleRepository.save(article);
    }

    @Test
    void testAddComment() {
        CommentArticle savedComment = commentService.addComment(article.getId(), "user123", "Nice article!");

        assertNotNull(savedComment.getId());
        assertEquals("user123", savedComment.getUsername());
        assertEquals("Nice article!", savedComment.getContent());
        assertEquals(article.getId(), savedComment.getArticle().getId());
    }

    @Test
    void testGetCommentsForArticle() {
        commentService.addComment(article.getId(), "reader1", "Insightful!");
        commentService.addComment(article.getId(), "reader2", "Thanks for sharing.");

        List<CommentArticle> comments = commentService.getCommentsForArticle(article.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getUsername().equals("reader1")));
        assertTrue(comments.stream().anyMatch(c -> c.getContent().equals("Thanks for sharing.")));
    }
}
