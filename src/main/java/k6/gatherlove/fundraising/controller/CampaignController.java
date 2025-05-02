package k6.gatherlove.fundraising.controller;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
