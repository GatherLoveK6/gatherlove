package k6.gatherlove.fundraising.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private CampaignService campaignService;

    private Campaign validCampaign;

    @BeforeEach
    void setUp() {
        validCampaign = CampaignFactory.createCampaign(
            "Help Orphans", "Fund for orphanage", 5000.0, "2025-12-31"
        );
    }

    @Test
    void shouldSaveValidCampaign() {
        when(campaignRepository.save(any(Campaign.class))).thenReturn(validCampaign);

        Campaign savedCampaign = campaignService.createCampaign(
            "Help Orphans", "Fund for orphanage", 5000.0, "2025-12-31"
        );

        assertThat(savedCampaign).isNotNull();
        assertThat(savedCampaign.getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);
        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void shouldNotSaveInvalidCampaign() {
        assertThrows(IllegalArgumentException.class, () -> campaignService.createCampaign(
            "", "", -100.0, "2020-01-01"
        ));
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void shouldReturnAllCampaigns() {
        when(campaignRepository.findAll()).thenReturn(List.of(validCampaign));

        List<Campaign> campaigns = campaignService.getAllCampaigns();

        assertThat(campaigns).isNotEmpty();
        assertThat(campaigns.size()).isEqualTo(1);
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnCampaignById() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(validCampaign));

        Optional<Campaign> foundCampaign = campaignService.getCampaignById(1L);

        assertThat(foundCampaign).isPresent();
        assertThat(foundCampaign.get().getTitle()).isEqualTo("Help Orphans");
        verify(campaignRepository, times(1)).findById(1L);
    }
}
