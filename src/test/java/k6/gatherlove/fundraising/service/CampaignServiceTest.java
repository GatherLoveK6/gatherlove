package k6.gatherlove.fundraising.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Mock
    private MultipartFile multipartFile;

    private CampaignCreationRequest validRequest;
    private CampaignCreationRequest invalidRequest;
    private CampaignUpdateRequest updateRequest;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        validRequest = new CampaignCreationRequest(
                "Help Orphans",
                "Supporting orphans with education and healthcare needs. This detailed description explains the campaign goals.",
                new BigDecimal("5000.00"),
                LocalDate.now().plusMonths(3)
        );

        invalidRequest = new CampaignCreationRequest(
                "",
                "",
                new BigDecimal("-100.00"),
                LocalDate.now().minusDays(1)
        );

        updateRequest = new CampaignUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description for campaign.");
        updateRequest.setGoalAmount(new BigDecimal("6000.00"));
        updateRequest.setDeadline(LocalDate.now().plusMonths(4));
    }

    @Test
    void shouldCreateCampaignWhenDataIsValid() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setTitle("Help Orphans");
        campaign.setDescription("Supporting orphans with education and healthcare needs. This detailed description explains the campaign goals.");
        campaign.setGoalAmount(new BigDecimal("5000.00"));
        campaign.setDeadline(LocalDate.now().plusMonths(3));
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);
        campaign.setUserId(userId);

        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        // Act
        Campaign result = campaignService.createCampaign(validRequest, userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenDataIsInvalid() {
        // Act & Assert
        assertThrows(ValidationException.class, 
                () -> campaignService.createCampaign(invalidRequest, userId));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }
    
    @Test
    void shouldGetCampaignsByUserId() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setTitle("Help Orphans");
        campaign.setUserId(userId);

        when(campaignRepository.findByUserId(userId)).thenReturn(List.of(campaign));

        // Act
        List<Campaign> result = campaignService.getCampaignsByUserId(userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Help Orphans");
    }
    
    @Test
    void shouldReturnEmptyListWhenNoUserCampaigns() {
        // Arrange
        when(campaignRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<Campaign> result = campaignService.getCampaignsByUserId(userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateCampaignWhenValid() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        // Act
        Campaign result = campaignService.updateCampaign(1L, updateRequest, userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description for campaign.");
        assertThat(result.getGoalAmount()).isEqualTo(new BigDecimal("6000.00"));
        assertThat(result.getDeadline()).isEqualTo(updateRequest.getDeadline());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateCampaignNotAllowed() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(2L); // Different user
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, () -> campaignService.updateCampaign(1L, updateRequest, userId));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldDeleteCampaignWhenValid() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act
        campaignService.deleteCampaign(1L, userId);

        // Assert
        verify(campaignRepository, times(1)).delete(campaign);
    }

    @Test
    void shouldThrowExceptionWhenDeleteCampaignNotAllowed() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(2L); // Not the same user
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, () -> campaignService.deleteCampaign(1L, userId));
        verify(campaignRepository, never()).delete(any(Campaign.class));
    }

    @Test
    void shouldUploadProofOfFundUsageWhenValid() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(multipartFile.getOriginalFilename()).thenReturn("proof.pdf");

        // Act
        campaignService.uploadProofOfFundUsage(1L, userId, multipartFile);

        // Assert
        assertThat(campaign.getProofFilePath()).isEqualTo("proofs/proof.pdf");
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void shouldThrowExceptionWhenUploadProofNotAllowed() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(2L); // Not the same user
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, () -> campaignService.uploadProofOfFundUsage(1L, userId, multipartFile));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldVerifyCampaignWhenPendingAndApproved() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act
        campaignService.verifyCampaign(1L, true);

        // Assert
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void shouldVerifyCampaignWhenPendingAndRejected() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act
        campaignService.verifyCampaign(1L, false);

        // Assert
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.REJECTED);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void shouldThrowExceptionWhenVerifyCampaignNotPending() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, () -> campaignService.verifyCampaign(1L, true));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldGetActiveCampaigns() {
        // Arrange
        Campaign activeCampaign = new Campaign();
        activeCampaign.setId(1L);
        activeCampaign.setStatus(CampaignStatus.ACTIVE);

        Campaign inactiveCampaign = new Campaign();
        inactiveCampaign.setId(2L);
        inactiveCampaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findAll()).thenReturn(List.of(activeCampaign, inactiveCampaign));

        // Act
        List<Campaign> result = campaignService.getActiveCampaigns();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }
}