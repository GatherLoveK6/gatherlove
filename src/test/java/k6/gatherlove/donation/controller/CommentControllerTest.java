package k6.gatherlove.donation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired private MockMvc mvc;

    @Test
    void addComment_Succeeds() throws Exception {
        String json = """
            {
              "userId": "user1",
              "text": "Great job!"
            }
            """;

        mvc.perform(post("/donations/campA/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())             // expecting 201 Created
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.text").value("Great job!"));
    }

    @Test
    void listComments_Succeeds() throws Exception {
        // create one comment
        String json = """
            {
              "userId": "u2",
              "text": "Nice work!"
            }
            """;
        mvc.perform(post("/donations/campB/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // fetch
        mvc.perform(get("/donations/campB/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Nice work!"));
    }
}
