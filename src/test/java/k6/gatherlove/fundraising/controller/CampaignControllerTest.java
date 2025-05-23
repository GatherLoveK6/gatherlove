package k6.gatherlove.fundraising.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController).build();
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void shouldCreateCampaignWhenDataIsValid() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
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
        createdCampaign.setUserId(1234L);

        when(campaignService.createCampaign(any(CampaignCreationRequest.class), eq(1234L)))
                .thenReturn(createdCampaign);

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
    }

    @Test
    void shouldReturnBadRequestWhenDataIsInvalid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        // Arrange
        CampaignCreationRequest request = new CampaignCreationRequest(
                "",
                "",
                new BigDecimal("-100.00"),
                LocalDate.now().minusDays(1)
        );

        when(campaignService.createCampaign(any(CampaignCreationRequest.class), eq(1234L)))
                .thenThrow(new ValidationException("Invalid campaign data"));

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid campaign data"));
    }

    @Test
    void shouldGetUserCampaigns() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        // Arrange
        List<Campaign> userCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Help Orphans");
        campaign.setUserId(1234L);
        userCampaigns.add(campaign);

        when(campaignService.getCampaignsByUserId(1234L)).thenReturn(userCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Help Orphans"));
    }
    
    @Test
    void shouldGetCampaignDetails() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Help Orphans");
        campaign.setUserId(1234L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"));
    }

    @Test
    void shouldUpdateCampaignWhenValid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
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

        when(campaignService.updateCampaign(eq(1L), any(CampaignUpdateRequest.class), eq(1234L)))
                .thenReturn(updatedCampaign);

        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateCampaignInvalid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        CampaignUpdateRequest updateRequest = new CampaignUpdateRequest();
        when(campaignService.updateCampaign(eq(1L), any(CampaignUpdateRequest.class), eq(1234L)))
                .thenThrow(new ValidationException("Update not allowed"));

        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Update not allowed"));
    }

    @Test
    void shouldDeleteCampaignWhenValid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        mockMvc.perform(delete("/api/fundraising/campaigns/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenDeleteCampaignInvalid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        doThrow(new ValidationException("Delete not allowed"))
                .when(campaignService).deleteCampaign(1L, 1234L);

        mockMvc.perform(delete("/api/fundraising/campaigns/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Delete not allowed"));
    }

    @Test
    void shouldUploadProofOfFundUsageWhenValid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenUploadProofInvalid() throws Exception {
        // Set up authentication for this specific test
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());
        doThrow(new ValidationException("Upload not allowed"))
                .when(campaignService).uploadProofOfFundUsage(1L, 1234L, file);

        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Upload not allowed"));
    }
}