package k6.gatherlove.fundraising.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
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
import org.springframework.mock.web.MockMultipartFile;
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
    
    @Test
    void shouldGetCampaignDetails() throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Help Orphans");

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"));
    }

    @Test
    void shouldReturnNotFoundWhenCampaignDoesNotExist() throws Exception {
        when(campaignService.getCampaignById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/fundraising/campaigns/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCampaignWhenValid() throws Exception {
        CampaignUpdateRequest updateRequest = new CampaignUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setGoalAmount(new BigDecimal("6000.00"));
        updateRequest.setDeadline(LocalDate.now().plusMonths(4));

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setId(1L);
        updatedCampaign.setTitle("Updated Title");
        updatedCampaign.setDescription("Updated Description");
        updatedCampaign.setGoalAmount(new BigDecimal("6000.00"));
        updatedCampaign.setDeadline(updateRequest.getDeadline());

        when(campaignService.updateCampaign(eq(1L), any(CampaignUpdateRequest.class), eq(1L)))
                .thenReturn(updatedCampaign);

        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", 1L)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateCampaignInvalid() throws Exception {
        CampaignUpdateRequest updateRequest = new CampaignUpdateRequest();
        when(campaignService.updateCampaign(eq(1L), any(CampaignUpdateRequest.class), eq(1L)))
                .thenThrow(new ValidationException("Update not allowed"));

        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", 1L)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Update not allowed"));
    }

    @Test
    void shouldDeleteCampaignWhenValid() throws Exception {
        mockMvc.perform(delete("/api/fundraising/campaigns/1")
                .header("userId", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenDeleteCampaignInvalid() throws Exception {
        doThrow(new ValidationException("Delete not allowed"))
                .when(campaignService).deleteCampaign(1L, 1L);

        mockMvc.perform(delete("/api/fundraising/campaigns/1")
                .header("userId", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Delete not allowed"));
    }

    @Test
    void shouldUploadProofOfFundUsageWhenValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file)
                .header("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenUploadProofInvalid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());
        doThrow(new ValidationException("Upload not allowed"))
                .when(campaignService).uploadProofOfFundUsage(1L, 1L, file);

        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file)
                .header("userId", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Upload not allowed"));
    }

    @Test
    void shouldVerifyCampaignWhenValid() throws Exception {
        mockMvc.perform(post("/api/fundraising/campaigns/1/verify")
                .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenVerifyCampaignInvalid() throws Exception {
        doThrow(new ValidationException("Verification not allowed"))
                .when(campaignService).verifyCampaign(1L, true);

        mockMvc.perform(post("/api/fundraising/campaigns/1/verify")
                .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Verification not allowed"));
    }

    @Test
    void shouldGetActiveCampaigns() throws Exception {
        List<Campaign> activeCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.ACTIVE);
        activeCampaigns.add(campaign);

        when(campaignService.getActiveCampaigns()).thenReturn(activeCampaigns);

        mockMvc.perform(get("/api/fundraising/campaigns/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
