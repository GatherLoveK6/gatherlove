package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleManagementServiceTest {

    private ArticleManagementRepository articleRepository;
    private ArticleManagementService articleService;
    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleManagementRepository.class);
        articleService = new ArticleManagementServiceImpl(articleRepository);

        article = ArticleManagementModel.builder()
                .id(1L)
                .title("Donation Best Practices")
                .content("Tips for safe and effective donations.")
                .author("adminUser")
                .build();
    }

    @Test
    void testCreateArticle() {
        when(articleRepository.save(article)).thenReturn(article);

        ArticleManagementModel saved = articleService.createArticle(article);
        assertEquals("Donation Best Practices", saved.getTitle());
    }

    @Test
    void testGetArticleById() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        ArticleManagementModel found = articleService.getArticleById(1L);
        assertEquals("adminUser", found.getAuthor());
    }

    @Test
    void testGetAllArticles() {
        when(articleRepository.findAll()).thenReturn(List.of(article));

        List<ArticleManagementModel> list = articleService.getAllArticles();
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateArticle() {
        ArticleManagementModel updated = ArticleManagementModel.builder()
                .title("Updated Title")
                .content("Updated Content")
                .author("adminUser")
                .build();

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(articleRepository.save(any())).thenReturn(updated);

        ArticleManagementModel result = articleService.updateArticle(1L, updated);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
    }

    @Test
    void testDeleteArticle() {
        doNothing().when(articleRepository).deleteById(1L);

        // Just call the method; no exception should be thrown
        assertDoesNotThrow(() -> articleService.deleteArticle(1L));

        verify(articleRepository, times(1)).deleteById(1L);
    }

}