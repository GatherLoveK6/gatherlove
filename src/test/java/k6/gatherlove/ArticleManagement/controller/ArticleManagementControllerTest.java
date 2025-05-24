package k6.gatherlove.ArticleManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.repository.ArticleManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ArticleManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleManagementRepository articleRepository;

    private ArticleManagementModel article;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        article = ArticleManagementModel.builder()
                .title("Test Title")
                .content("Some content")
                .author("Admin")
                .likes(0)
                .build();
        article = articleRepository.save(article);
    }

    @Test
    @WithMockUser
    void testGetAllArticles() throws Exception {
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Title")));
    }

    @Test
    @WithMockUser
    void testCreateArticle() throws Exception {
        ArticleManagementModel newArticle = ArticleManagementModel.builder()
                .title("New Article")
                .content("New Content")
                .author("Tester")
                .build();

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newArticle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Article"));
    }

    @Test
    @WithMockUser
    void testUpdateArticle() throws Exception {
        article.setTitle("Updated Title");

        mockMvc.perform(put("/articles/" + article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser
    void testDeleteArticle() throws Exception {
        mockMvc.perform(delete("/articles/" + article.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testLikeArticle() throws Exception {
        mockMvc.perform(post("/articles/" + article.getId() + "/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1));
    }

    @Test
    @WithMockUser
    void testAddComment() throws Exception {
        mockMvc.perform(post("/articles/" + article.getId() + "/comments")
                        .param("username", "testuser")
                        .param("content", "Great article!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.content").value("Great article!"));
    }

    @Test
    @WithMockUser
    void testGetComments() throws Exception {

        mockMvc.perform(post("/articles/" + article.getId() + "/comments")
                .param("username", "testuser")
                .param("content", "Great article!"));


        mockMvc.perform(get("/articles/" + article.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].content").value("Great article!"));
    }
}
