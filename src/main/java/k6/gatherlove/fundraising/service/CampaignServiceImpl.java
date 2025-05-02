package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.factory.CampaignFactory;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import k6.gatherlove.fundraising.validator.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    
    private final CampaignRepository campaignRepository;
    
    @Override
    public Campaign createCampaign(CampaignCreationRequest request, Long userId) {
        CampaignValidator.validate(request);
        Campaign campaign = CampaignFactory.createCampaign(request, userId);
        return campaignRepository.save(campaign);
    }
    
    @Override
    public List<Campaign> getCampaignsByUserId(Long userId) {
        return campaignRepository.findByUserId(userId);
    }
    
    @Override
    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }
    
    @Override
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }
}
