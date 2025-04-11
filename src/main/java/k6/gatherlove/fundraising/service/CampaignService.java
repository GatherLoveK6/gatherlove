package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.model.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignService {
    Campaign createCampaign(CampaignCreationRequest request, Long userId);
    List<Campaign> getCampaignsByUserId(Long userId);
    Optional<Campaign> getCampaignById(Long id);
    List<Campaign> getAllCampaigns();
}
