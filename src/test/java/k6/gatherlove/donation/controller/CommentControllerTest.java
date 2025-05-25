package k6.gatherlove.donation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.client.WalletClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import(CommentControllerTest.TestConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void addComment_Succeeds() throws Exception {
        String payload = """
          {
            "userId": "user1",
            "text": "Great job!"
          }
          """;

        mvc.perform(post("/donations/campA/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.text").value("Great job!"));
    }

    @Test
    void listComments_Succeeds() throws Exception {
        // create a comment
        String payload = """
          {
            "userId": "u2",
            "text": "Nice work!"
          }
          """;

        mvc.perform(post("/donations/campB/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        // list comments for campB
        mvc.perform(get("/donations/campB/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campaignId").value("campB"))
                .andExpect(jsonPath("$[0].userId").value("u2"))
                .andExpect(jsonPath("$[0].text").value("Nice work!"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        WalletClient walletClient() {
            return new WalletClient() {
                @Override
                public void deduct(String userId, double amount) {
                    // no-op
                }
                @Override
                public void refund(String userId, double amount) {
                    // no-op
                }
            };
        }

        @Bean
        CampaignClient campaignClient() {
            return new CampaignClient() {
                @Override
                public void recordDonation(String campaignId, String donationId, double amount) {
                    // no-op
                }
                @Override
                public void removeDonation(String campaignId, String donationId) {
                    // no-op
                }
            };
        }
    }
}