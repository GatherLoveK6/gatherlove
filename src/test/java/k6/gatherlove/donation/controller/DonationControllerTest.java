package k6.gatherlove.donation.controller;

import com.jayway.jsonpath.JsonPath;
import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.client.WalletClient;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import(DonationControllerTest.TestConfig.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void createDonation_Succeeds() throws Exception {
        String payload = """
          {
            "userId": "user1",
            "campaignId": "campA",
            "amount": 10.0
          }
          """;

        MvcResult res = mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.amount").value(10.0))
                .andExpect(jsonPath("$.confirmed").value(true))
                .andReturn();

        String id = JsonPath.read(res.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotBlank();
    }

    @Test
    void getDonationById_Succeeds() throws Exception {
        String payload = """
          {
            "userId": "user1",
            "campaignId": "campA",
            "amount": 10.0
          }
          """;

        MvcResult created = mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mvc.perform(get("/donations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.amount").value(10.0));
    }

    @Test
    void getDonationById_NotFound() throws Exception {
        mvc.perform(get("/donations/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        WalletClient walletClient() {
            return new WalletClient() {
                @Override public void deduct(String userId, double amount) { }
                @Override public void refund(String userId, double amount) { }
            };
        }

        @Bean
        CampaignClient campaignClient() {
            return new CampaignClient() {
                @Override public void recordDonation(String campaignId, String donationId, double amount) { }
                @Override public void removeDonation(String campaignId, String donationId) { }
            };
        }
    }
}
