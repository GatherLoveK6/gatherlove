package k6.gatherlove.fundraising.controller;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fundraising")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {
    
    private final CampaignService campaignService;
    
    @PostMapping("/campaigns")
    public ResponseEntity<?> createCampaign(@RequestBody CampaignCreationRequest request,
                                           @RequestHeader("userId") Long userId) {
        try {
            Campaign createdCampaign = campaignService.createCampaign(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
        } catch (ValidationException e) {
            log.error("Validation error creating campaign for user {}: {}", userId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/campaigns/my-campaigns")
    public ResponseEntity<List<Campaign>> getUserCampaigns(@RequestHeader("userId") Long userId) {
        List<Campaign> userCampaigns = campaignService.getCampaignsByUserId(userId);
        return ResponseEntity.ok(userCampaigns);
    }
    
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaignDetails(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/campaigns/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id,
                                            @RequestBody CampaignUpdateRequest request,
                                            @RequestHeader("userId") Long userId) {
        try {
            Campaign updated = campaignService.updateCampaign(id, request, userId);
            return ResponseEntity.ok(updated);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        try {
            campaignService.deleteCampaign(id, userId);
            return ResponseEntity.noContent().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/campaigns/{id}/proof")
    public ResponseEntity<?> uploadProofOfFundUsage(@PathVariable Long id,
                                                    @RequestHeader("userId") Long userId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            campaignService.uploadProofOfFundUsage(id, userId, file);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/campaigns/{id}/verify")
    public ResponseEntity<?> verifyCampaign(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            campaignService.verifyCampaign(id, approved);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/campaigns/active")
    public ResponseEntity<List<Campaign>> getActiveCampaigns() {
        return ResponseEntity.ok(campaignService.getActiveCampaigns());
    }

}
