package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.factory.CampaignFactory;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.repository.CampaignRepository;
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
        validateCampaignRequest(request);
        
        Campaign campaign = CampaignFactory.createCampaign(request, userId);
        
        return campaignRepository.save(campaign);
    }
    
    private void validateCampaignRequest(CampaignCreationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
        
        if (request.getDescription().trim().length() < 20) {
            throw new ValidationException("Description must be at least 20 characters");
        }
        
        if (request.getGoalAmount() == null || request.getGoalAmount().signum() <= 0) {
            throw new ValidationException("Goal amount must be greater than zero");
        }
        
        if (request.getDeadline() == null || request.getDeadline().isBefore(LocalDate.now())) {
            throw new ValidationException("Deadline must be in the future");
        }
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
