package k6.gatherlove.donation.controller;

import com.jayway.jsonpath.JsonPath;
import k6.gatherlove.donation.client.CampaignClient;
import k6.gatherlove.donation.client.WalletClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser   // bypass security
class DonationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WalletClient walletClient;

    @MockBean
    private CampaignClient campaignClient;

    @Test
    void createDonation_Succeeds() throws Exception {
        // stub every argument with matchers
        doNothing().when(walletClient).deduct(anyString(), anyDouble());
        doNothing().when(campaignClient)
                .recordDonation(anyString(), anyString(), anyDouble());

        mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                   {"userId":"user1","campaignId":"campA","amount":10.0}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.campaignId").value("campA"))
                .andExpect(jsonPath("$.amount").value(10.0))
                .andExpect(jsonPath("$.confirmed").value(true));
    }

    @Test
    void getDonationById_Succeeds() throws Exception {
        doNothing().when(walletClient).deduct(anyString(), anyDouble());
        doNothing().when(campaignClient)
                .recordDonation(anyString(), anyString(), anyDouble());

        // create a donation
        MvcResult createResult = mvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                           {"userId":"user1","campaignId":"campA","amount":10.0}
                        """))
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        // fetch it
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