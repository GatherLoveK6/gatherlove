package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import k6.gatherlove.service.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private FileStorageService fileStorageService;
    
    @Mock
    private MetricsService metricsService;
    
    @Mock
    private Timer.Sample timerSample;

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
        
        campaignService = new CampaignServiceImpl(
                campaignRepository,
                fileStorageService,
                metricsService
        );
        
        // Only mock timer when needed in specific tests, not globally
    }

    @Test
    void shouldCreateCampaignWhenDataIsValid() {
        // Arrange
        when(metricsService.startCampaignProcessingTimer()).thenReturn(timerSample);
        
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
        verify(metricsService).startCampaignProcessingTimer();
        verify(metricsService).incrementCampaignCreated();
        verify(metricsService).recordCampaignProcessing(timerSample);
    }

    @Test
    void shouldThrowExceptionWhenDataIsInvalid() {
        // Arrange
        when(metricsService.startCampaignProcessingTimer()).thenReturn(timerSample);
        
        // Act & Assert
        assertThrows(ValidationException.class, 
                () -> campaignService.createCampaign(invalidRequest, userId));
        verify(campaignRepository, never()).save(any(Campaign.class));
        
        verify(metricsService).startCampaignProcessingTimer();
        verify(metricsService).recordCampaignProcessing(timerSample);
        verify(metricsService, never()).incrementCampaignCreated();
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
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(fileStorageService.storeFile(multipartFile, "proofs")).thenReturn("proofs/test-file.jpg");

        // Act
        campaignService.uploadProofOfFundUsage(1L, userId, multipartFile);

        // Assert
        assertThat(campaign.getProofFilePath()).isEqualTo("proofs/test-file.jpg");
        verify(campaignRepository, times(1)).save(campaign);
        verify(fileStorageService, times(1)).storeFile(multipartFile, "proofs");
    }

    @Test
    void shouldThrowExceptionWhenUploadProofWithInvalidFileType() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.uploadProofOfFundUsage(1L, userId, multipartFile));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenUploadProofWithNullContentType() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(multipartFile.getContentType()).thenReturn(null);

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.uploadProofOfFundUsage(1L, userId, multipartFile));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenUploadProofCampaignNotActive() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.uploadProofOfFundUsage(1L, userId, multipartFile));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundForUpload() {
        // Arrange
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.uploadProofOfFundUsage(1L, userId, multipartFile));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundForUpdate() {
        // Arrange
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.updateCampaign(1L, updateRequest, userId));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateCampaignNotPending() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.updateCampaign(1L, updateRequest, userId));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundForDelete() {
        // Arrange
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.deleteCampaign(1L, userId));
        verify(campaignRepository, never()).delete(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteCampaignNotPending() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setUserId(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.deleteCampaign(1L, userId));
        verify(campaignRepository, never()).delete(any(Campaign.class));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFoundForVerification() {
        // Arrange
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> campaignService.verifyCampaign(1L, true));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldGetPendingCampaigns() {
        // Arrange
        Campaign pendingCampaign = new Campaign();
        pendingCampaign.setId(1L);
        pendingCampaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        Campaign activeCampaign = new Campaign();
        activeCampaign.setId(2L);
        activeCampaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findAll()).thenReturn(List.of(pendingCampaign, activeCampaign));

        // Act
        List<Campaign> result = campaignService.getPendingCampaigns();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveCampaigns() {
        // Arrange
        Campaign pendingCampaign = new Campaign();
        pendingCampaign.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findAll()).thenReturn(List.of(pendingCampaign));

        // Act
        List<Campaign> result = campaignService.getActiveCampaigns();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoPendingCampaigns() {
        // Arrange
        Campaign activeCampaign = new Campaign();
        activeCampaign.setStatus(CampaignStatus.ACTIVE);

        when(campaignRepository.findAll()).thenReturn(List.of(activeCampaign));

        // Act
        List<Campaign> result = campaignService.getPendingCampaigns();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetAllCampaigns() {
        // Arrange
        Campaign campaign1 = new Campaign();
        campaign1.setId(1L);
        campaign1.setStatus(CampaignStatus.ACTIVE);

        Campaign campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setStatus(CampaignStatus.PENDING_VERIFICATION);

        when(campaignRepository.findAll()).thenReturn(List.of(campaign1, campaign2));

        // Act
        List<Campaign> result = campaignService.getAllCampaigns();

        // Assert
        assertThat(result).hasSize(2);
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCampaignById() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setTitle("Test Campaign");

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // Act
        Optional<Campaign> result = campaignService.getCampaignById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Campaign");
        verify(campaignRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenCampaignNotFound() {
        // Arrange
        when(campaignRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Campaign> result = campaignService.getCampaignById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(campaignRepository, times(1)).findById(999L);
    }

}