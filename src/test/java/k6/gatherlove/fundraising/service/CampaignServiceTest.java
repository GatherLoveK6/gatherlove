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

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private CampaignCreationRequest validRequest;
    private CampaignCreationRequest invalidRequest;
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
}
