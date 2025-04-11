package k6.gatherlove.ArticleManagement.model;

import k6.gatherlove.user.User;
import k6.gatherlove.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArticleManagementModelTest {

    private User admin;
    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(1L)
                .username("adminUser")
                .password("securepass")
                .role(Role.ADMIN)
                .build();

        article = ArticleManagementModel.builder()
                .id(101L)
                .title("Transparency in Fundraising")
                .content("This article explains how transparency is crucial.")
                .authorName("adminUser")
                .build();
    }

    @Test
    void testArticleFieldsInitialization() {
        assertEquals("Transparency in Fundraising", article.getTitle());
        assertEquals("This article explains how transparency is crucial.", article.getContent());
        assertEquals("adminUser", article.getAuthorName());
    }

    @Test
    void testUpdateTitle() {
        article.setTitle("Updated Title");
        assertEquals("Updated Title", article.getTitle());
    }

    @Test
    void testUpdateContent() {
        article.setContent("Updated content body.");
        assertEquals("Updated content body.", article.getContent());
    }
}
