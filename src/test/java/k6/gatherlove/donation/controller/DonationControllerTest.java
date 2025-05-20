package k6.gatherlove.donation.controller;

import k6.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DonationControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void createDonation_Succeeds() throws Exception {
        String body = """
            {
              "userId": "user1",
              "campaignId": "campA",
              "amount": 10.0
            }
            """;

        mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.amount").value(10.0))
                .andExpect(jsonPath("$.confirmed").value(true));
    }
}
