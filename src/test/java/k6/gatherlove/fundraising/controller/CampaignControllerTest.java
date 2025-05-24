package k6.gatherlove.fundraising.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.service.CampaignService;
import k6.gatherlove.fundraising.service.FileStorageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Mock
    private FileStorageService fileStorageService;

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
    }

    @Test
    void shouldCreateCampaignWhenDataIsValid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
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
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
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
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
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
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Help Orphans");
        campaign.setUserId(1234L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"));
    }

    @Test
    void shouldUpdateCampaignWhenValid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
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

        // Act & Assert
        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateCampaignInvalid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        CampaignUpdateRequest updateRequest = new CampaignUpdateRequest();
        when(campaignService.updateCampaign(eq(1L), any(CampaignUpdateRequest.class), eq(1234L)))
                .thenThrow(new ValidationException("Update not allowed"));

        // Act & Assert
        mockMvc.perform(put("/api/fundraising/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Update not allowed"));
    }

    @Test
    void shouldDeleteCampaignWhenValid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        // Act & Assert
        mockMvc.perform(delete("/api/fundraising/campaigns/1"))
                .andExpect(status().isNoContent());
        
        verify(campaignService, times(1)).deleteCampaign(1L, 1234L);
    }

    @Test
    void shouldReturnBadRequestWhenDeleteCampaignInvalid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        doThrow(new ValidationException("Delete not allowed"))
                .when(campaignService).deleteCampaign(1L, 1234L);

        // Act & Assert
        mockMvc.perform(delete("/api/fundraising/campaigns/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Delete not allowed"));
    }

    @Test
    void shouldUploadProofOfFundUsageWhenValid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file))
                .andExpect(status().isOk());
        
        verify(campaignService, times(1)).uploadProofOfFundUsage(1L, 1234L, file);
    }

    @Test
    void shouldReturnBadRequestWhenUploadProofInvalid() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", "dummy".getBytes());
        doThrow(new ValidationException("Upload not allowed"))
                .when(campaignService).uploadProofOfFundUsage(1L, 1234L, file);

        // Act & Assert
        mockMvc.perform(multipart("/api/fundraising/campaigns/1/proof")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Upload not allowed"));
    }

    @Test
    void shouldServeFile() throws Exception {
        // Arrange - no authentication needed for file serving
        Path testFile = Path.of("test-file.jpg");
        when(fileStorageService.getFilePath("proofs/test-file.jpg")).thenReturn(testFile);

        // Create a temporary file for testing
        Files.createFile(testFile);
        
        try {
            // Act & Assert
            mockMvc.perform(get("/api/fundraising/files/proofs/test-file.jpg"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"test-file.jpg\""));
        } finally {
            // Cleanup
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void shouldReturnNotFoundForNonExistentFile() throws Exception {
        // Arrange - no authentication needed for file serving
        Path testFile = Path.of("non-existent.jpg");
        when(fileStorageService.getFilePath("proofs/non-existent.jpg")).thenReturn(testFile);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/files/proofs/non-existent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldVerifyCampaignAsApproved() throws Exception {
        // Arrange - no authentication needed for verification (admin-only in real system)
        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns/1/verify")
                .param("approved", "true"))
                .andExpect(status().isOk());

        verify(campaignService, times(1)).verifyCampaign(1L, true);
    }

    @Test
    void shouldVerifyCampaignAsRejected() throws Exception {
        // Arrange - no authentication needed for verification (admin-only in real system)
        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns/1/verify")
                .param("approved", "false"))
                .andExpect(status().isOk());

        verify(campaignService, times(1)).verifyCampaign(1L, false);
    }

    @Test
    void shouldReturnBadRequestWhenVerificationFails() throws Exception {
        // Arrange - no authentication needed for verification
        doThrow(new ValidationException("Verification failed"))
                .when(campaignService).verifyCampaign(1L, true);

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns/1/verify")
                .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Verification failed"));
    }

    @Test
    void shouldReturnActiveCampaigns() throws Exception {
        // Arrange - no authentication needed for public active campaigns
        List<Campaign> activeCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Active Campaign");
        campaign.setStatus(CampaignStatus.ACTIVE);
        activeCampaigns.add(campaign);

        when(campaignService.getActiveCampaigns()).thenReturn(activeCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Active Campaign"));
    }

    @Test
    void shouldReturnAllCampaigns() throws Exception {
        // Arrange - no authentication needed for public campaigns
        List<Campaign> allCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Test Campaign");
        allCampaigns.add(campaign);

        when(campaignService.getAllCampaigns()).thenReturn(allCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Campaign"));
    }

    @Test
    void shouldReturnPendingCampaignsForAdmin() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();

        List<Campaign> allCampaigns = new ArrayList<>();
        Campaign pendingCampaign = new Campaign();
        pendingCampaign.setId(1L);
        pendingCampaign.setStatus(CampaignStatus.PENDING_VERIFICATION);
        allCampaigns.add(pendingCampaign);

        when(campaignService.getAllCampaigns()).thenReturn(allCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnForbiddenForNonAdminAccessingPending() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/pending"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenCampaignDoesNotExist() throws Exception {
        // Arrange - no authentication needed for this test
        when(campaignService.getCampaignById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleInternalServerErrorForActiveCampaigns() throws Exception {
        // Arrange - no authentication needed for public campaigns
        when(campaignService.getActiveCampaigns()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/active"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching active campaigns: Database error"));
    }

    @Test
    void shouldHandleInternalServerErrorForPendingCampaigns() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(campaignService.getAllCampaigns()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/pending"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleFileServingException() throws Exception {
        // Arrange - no authentication needed for file serving
        when(fileStorageService.getFilePath("proofs/error-file.jpg"))
                .thenThrow(new RuntimeException("File system error"));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/files/proofs/error-file.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleNonNumericUserIdInAuthentication() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user-uuid-123"); // Non-numeric ID
        
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
        createdCampaign.setUserId(Long.valueOf(Math.abs("user-uuid-123".hashCode()))); // Expected hashed ID

        when(campaignService.createCampaign(any(CampaignCreationRequest.class), anyLong()))
                .thenReturn(createdCampaign);

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Help Orphans"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotAuthenticated() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Please log in to view your campaigns"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationIsNull() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Please log in to view your campaigns"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Please log in to view your campaigns"));
    }

    @Test
    void shouldHandleExceptionInGetUserCampaigns() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        when(campaignService.getCampaignsByUserId(1234L))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/my-campaigns"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching campaigns: Database connection failed"));
    }

    @Test
    void shouldReturnCampaignForAuthenticatedOwner() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("My Pending Campaign");
        campaign.setUserId(1234L);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("My Pending Campaign"));
    }

    @Test
    void shouldReturnActiveCampaignForUnauthenticatedUser() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Active Campaign");
        campaign.setUserId(5678L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Active Campaign"));
    }

    @Test
    void shouldReturnNotFoundForNonActiveCampaignToUnauthenticatedUser() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Pending Campaign");
        campaign.setUserId(5678L);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForNonOwnerNonActiveCampaign() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1234");
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Other User's Pending Campaign");
        campaign.setUserId(5678L); // Different user
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFallbackToActiveCampaignOnException() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenThrow(new RuntimeException("Auth system error"));
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Active Campaign");
        campaign.setUserId(5678L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Active Campaign"));
    }

    @Test
    void shouldReturnNotFoundOnExceptionWithNonActiveCampaign() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenThrow(new RuntimeException("Auth system error"));
        
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Pending Campaign");
        campaign.setUserId(5678L);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        mockMvc.perform(get("/api/fundraising/campaigns/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCampaignWithNonNumericUserId() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test-user-uuid");
        
        CampaignCreationRequest request = new CampaignCreationRequest(
                "Test Campaign",
                "Test description for campaign creation",
                new BigDecimal("1000.00"),
                LocalDate.now().plusMonths(2)
        );

        Campaign createdCampaign = new Campaign();
        createdCampaign.setId(1L);
        createdCampaign.setTitle("Test Campaign");

        // The hashed ID will be used
        long expectedHashedId = Math.abs("test-user-uuid".hashCode());
        when(campaignService.createCampaign(any(CampaignCreationRequest.class), eq(expectedHashedId)))
                .thenReturn(createdCampaign);

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Campaign"));
    }

    @Test
    void shouldThrowValidationExceptionWhenNotAuthenticated() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        
        CampaignCreationRequest request = new CampaignCreationRequest(
                "Test Campaign",
                "Test description for campaign creation",
                new BigDecimal("1000.00"),
                LocalDate.now().plusMonths(2)
        );

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not authenticated"));
    }

    @Test
    void shouldThrowValidationExceptionWhenAuthenticationIsAnonymous() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        CampaignCreationRequest request = new CampaignCreationRequest(
                "Test Campaign",
                "Test description for campaign creation",
                new BigDecimal("1000.00"),
                LocalDate.now().plusMonths(2)
        );

        // Act & Assert
        mockMvc.perform(post("/api/fundraising/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not authenticated"));
    }
}