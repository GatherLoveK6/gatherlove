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
    void setUp() {
        article = ArticleManagementModel.builder()
                .id(1L)
                .title("Why Donations Matter")
                .content("Content about the impact of donations.")
                .author("adminUser")
                .build();
    }

    @Test
    void testGetAllArticles() throws Exception {
        when(articleService.getAllArticles()).thenReturn(List.of(article));

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Why Donations Matter"));
    }

    @Test
    void testCreateArticle() throws Exception {
        when(articleService.createArticle(Mockito.any())).thenReturn(article);

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(article)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("adminUser"));
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
