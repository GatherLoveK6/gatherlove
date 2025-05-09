package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.model.Campaign;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface CampaignService {
    Campaign createCampaign(CampaignCreationRequest request, Long userId);
    List<Campaign> getCampaignsByUserId(Long userId);
    Optional<Campaign> getCampaignById(Long id);
    List<Campaign> getAllCampaigns();

    Campaign updateCampaign(Long id, CampaignUpdateRequest request, Long userId);
    void deleteCampaign(Long id, Long userId);
    void uploadProofOfFundUsage(Long id, Long userId, MultipartFile file);
    void verifyCampaign(Long id, boolean approved);
    List<Campaign> getActiveCampaigns();
}
