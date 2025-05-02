package k6.gatherlove.ArticleManagement.repository;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleManagementRepositoryTest {

    private ArticleManagementRepository articleRepository;

    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleManagementRepository.class);

        article = ArticleManagementModel.builder()
                .id(1L)
                .title("Why Transparency Matters in Donations")
                .content("Transparency builds trust and encourages recurring contributions.")
                .author("adminUser")
                .build();
    }

    @Test
    void testSaveArticle() {
        when(articleRepository.save(article)).thenReturn(article);

        ArticleManagementModel saved = articleRepository.save(article);

        assertNotNull(saved);
        assertEquals("Why Transparency Matters in Donations", saved.getTitle());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void testFindAll() {
        when(articleRepository.findAll()).thenReturn(List.of(article));

        List<ArticleManagementModel> articles = articleRepository.findAll();

        assertFalse(articles.isEmpty());
        assertEquals(1, articles.size());
        verify(articleRepository, times(1)).findAll();
    }
}
