package k6.gatherlove.ArticleManagement.repository;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.user.User;
import k6.gatherlove.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        User admin = User.builder()
                .id(1L)
                .username("adminUser")
                .password("securepass")
                .role(Role.ADMIN)
                .build();

        article = ArticleManagementModel.builder()
                .title("Why Transparency Matters in Donations")
                .content("Transparency builds trust and encourages recurring contributions.")
                .author(admin)
                .build();
    }

    @Test
    void testSaveArticle() {
        ArticleManagementModel saved = articleRepository.save(article);
        assertNotNull(saved.getId());
        assertEquals("Why Transparency Matters in Donations", saved.getTitle());
    }

    @Test
    void testFindAll() {
        articleRepository.save(article);
        List<ArticleManagementModel> list = articleRepository.findAll();
        assertFalse(list.isEmpty());
    }
}

