package k6.gatherlove.fundraising.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController).build();
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateCampaignWhenDataIsValid() throws Exception {
        // Arrange
        CampaignCreationRequest request = new CampaignCreationRequest(
                "Help Orphans",
                "Supporting orphans with education and healthcare needs.",
                new BigDecimal("5000.00"),
                LocalDate.now().plusMonths(3)
        );

        Campaign createdCampaign = new Campaign();
        createdCampaign.setId(1L);
        createdCampaign.setTitle("Help Orphans");
        createdCampaign.setDescription("Supporting orphans with education and healthcare needs.");
        createdCampaign.setGoalAmount(new BigDecimal("5000.00"));
        createdCampaign.setDeadline(LocalDate.now().plusMonths(3));
        createdCampaign.setStatus(CampaignStatus.PENDING_VERIFICATION);
        createdCampaign.setUserId(1L);

        when(campaignService.createCampaign(any(CampaignCreationRequest.class), eq(1L)))
                .thenReturn(createdCampaign);

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", 1L)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
    }

    @Test
    void shouldReturnBadRequestWhenDataIsInvalid() throws Exception {
        // Arrange
        CampaignCreationRequest request = new CampaignCreationRequest(
                "",
                "",
                new BigDecimal("-100.00"),
                LocalDate.now().minusDays(1)
        );

        when(campaignService.createCampaign(any(CampaignCreationRequest.class), eq(1L)))
                .thenThrow(new ValidationException("Invalid campaign data"));

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", 1L)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid campaign data"));
    }

    @Test
    void shouldGetUserCampaigns() throws Exception {
        // Arrange
        List<Campaign> userCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Help Orphans");
        campaign.setUserId(1L);
        userCampaigns.add(campaign);

        when(campaignService.getCampaignsByUserId(1L)).thenReturn(userCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns")
                .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Help Orphans"));
    }
}
