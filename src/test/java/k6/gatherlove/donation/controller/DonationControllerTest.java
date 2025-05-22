package k6.gatherlove.donation.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DonationControllerTest {

    @Autowired
    MockMvc mvc;

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
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.amount").value(10.0))
                .andExpect(jsonPath("$.confirmed").value(true));
    }

    @Test
    void getDonationById_Succeeds() throws Exception {
        // create a donation
        String body = """
            {
              "userId": "user1",
              "campaignId": "campA",
              "amount": 10.0
            }
            """;

        MvcResult createResult = mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String json = createResult.getResponse().getContentAsString();
        String id   = JsonPath.read(json, "$.id");

        // fetch
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
}
