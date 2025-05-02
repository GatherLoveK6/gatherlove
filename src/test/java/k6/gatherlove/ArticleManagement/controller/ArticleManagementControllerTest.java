package k6.gatherlove.ArticleManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.service.ArticleManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleManagementController.class)
public class ArticleManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleManagementService articleService;

    private ArticleManagementModel article;

    @BeforeEach
    void setup() {
        article = new ArticleManagementModel();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setAuthor("Admin");
    }

    @Test
    void testGetAllArticles() throws Exception {
        when(articleService.getAllArticles()).thenReturn(List.of(article));

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateArticle() throws Exception {
        when(articleService.createArticle(any())).thenReturn(article);

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(article)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateArticle() throws Exception {
        when(articleService.updateArticle(any(), any())).thenReturn(article);

        mockMvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(article)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteArticle() throws Exception {
        Mockito.doNothing().when(articleService).deleteArticle(1L);

        mockMvc.perform(delete("/articles/1"))
                .andExpect(status().isNoContent());
    }
}
