package k6.gatherlove.fundraising.service;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.factory.CampaignFactory;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import k6.gatherlove.fundraising.validator.CampaignValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService {
    
    private final CampaignRepository campaignRepository;
    private final FileStorageService fileStorageService;
    
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
@Override
    public Campaign updateCampaign(Long id, CampaignUpdateRequest request, Long userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Campaign not found"));
        if (!campaign.getUserId().equals(userId) || campaign.getStatus() != CampaignStatus.PENDING_VERIFICATION) {
            throw new ValidationException("You can only update your own pending campaigns");
        }
        CampaignFactory.updateCampaign(campaign, request);
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(Long id, Long userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Campaign not found"));
        if (!campaign.getUserId().equals(userId) || campaign.getStatus() != CampaignStatus.PENDING_VERIFICATION) {
            throw new ValidationException("You can only delete your own pending campaigns");
        }
        campaignRepository.delete(campaign);
    }

    @Override
    public void uploadProofOfFundUsage(Long id, Long userId, MultipartFile file) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Campaign not found"));
        if (!campaign.getUserId().equals(userId) || campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new ValidationException("You can only upload proof for your active campaigns");
        }
        
        // Validate file type (optional - add image validation)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("Only image files are allowed");
        }
        
        // Store the file using FileStorageService
        String filePath = fileStorageService.storeFile(file, "proofs");
        campaign.setProofFilePath(filePath);
        
        log.info("Proof uploaded for campaign {}: {}", id, filePath);
        campaignRepository.save(campaign);
    }

    @Override
    public void verifyCampaign(Long id, boolean approved) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.PENDING_VERIFICATION) {
            throw new ValidationException("Only pending campaigns can be verified");
        }
        campaign.setStatus(approved ? CampaignStatus.ACTIVE : CampaignStatus.REJECTED);
        campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findAll().stream()
                .filter(c -> c.getStatus() == CampaignStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Campaign> getPendingCampaigns() {
        return campaignRepository.findAll().stream()
                .filter(c -> c.getStatus() == CampaignStatus.PENDING_VERIFICATION)
                .collect(Collectors.toList());
    }
}

