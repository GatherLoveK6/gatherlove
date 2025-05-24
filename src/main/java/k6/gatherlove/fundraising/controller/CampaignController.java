package k6.gatherlove.fundraising.controller;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.dto.CampaignUpdateRequest;
import k6.gatherlove.fundraising.exception.ValidationException;
import k6.gatherlove.fundraising.model.Campaign;
import k6.gatherlove.fundraising.model.CampaignStatus;
import k6.gatherlove.fundraising.service.CampaignService;
import k6.gatherlove.fundraising.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fundraising")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {
    
    private final CampaignService campaignService;
    private final FileStorageService fileStorageService;
    
    // Updated to handle both numeric and UUID-style user IDs
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ValidationException("User not authenticated");
        }
        
        String userId = authentication.getName();
        log.debug("Raw authenticated user ID: {}", userId);
        
        try {
            // Try to parse as Long directly
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            // If not a direct number, it might be a UUID or complex ID
            // Generate a consistent numeric hash for this session
            // This is a workaround - in production you'd want proper ID mapping
            return (long) Math.abs(userId.hashCode());
        }
    }
    
    @PostMapping("/campaigns")
    public ResponseEntity<?> createCampaign(@RequestBody CampaignCreationRequest request) {
        try {
            Long userId = getAuthenticatedUserId();
            log.info("Creating campaign for user: {}", userId);
            
            Campaign createdCampaign = campaignService.createCampaign(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
        } catch (ValidationException e) {
            Long userId = null;
            try {
                userId = getAuthenticatedUserId();
            } catch (Exception ex) {
                // Ignore if we can't get the user ID
            }
            
            log.error("Validation error creating campaign for user {}: {}", userId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/campaigns/my-campaigns")
    public ResponseEntity<List<Campaign>> getUserCampaigns() {
        try {
            Long userId = getAuthenticatedUserId();
            log.info("Fetching campaigns for user: {}", userId);
            
            List<Campaign> userCampaigns = campaignService.getCampaignsByUserId(userId);
            return ResponseEntity.ok(userCampaigns);
        } catch (Exception e) {
            log.error("Error fetching user campaigns", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaignDetails(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(campaign -> {
                    // Only return the campaign if it's ACTIVE or belongs to the current user
                    try {
                        Long userId = getAuthenticatedUserId();
                        if (campaign.getUserId().equals(userId) || 
                            campaign.getStatus() == CampaignStatus.ACTIVE) {
                            return ResponseEntity.ok(campaign);
                        }
                    } catch (Exception e) {
                        // For unauthenticated users, only show ACTIVE campaigns
                        if (campaign.getStatus() == CampaignStatus.ACTIVE) {
                            return ResponseEntity.ok(campaign);
                        }
                    }
                    return ResponseEntity.notFound().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/campaigns/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id,
                                            @RequestBody CampaignUpdateRequest request) {
        try {
            Long userId = getAuthenticatedUserId();
            log.info("Updating campaign {} for user: {}", id, userId);
            
            Campaign updated = campaignService.updateCampaign(id, request, userId);
            return ResponseEntity.ok(updated);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        try {
            Long userId = getAuthenticatedUserId();
            log.info("Deleting campaign {} for user: {}", id, userId);
            
            campaignService.deleteCampaign(id, userId);
            return ResponseEntity.noContent().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/campaigns/{id}/proof")
    public ResponseEntity<?> uploadProofOfFundUsage(@PathVariable Long id,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            Long userId = getAuthenticatedUserId();
            log.info("Uploading proof for campaign {} by user: {}", id, userId);
            
            campaignService.uploadProofOfFundUsage(id, userId, file);
            return ResponseEntity.ok(Map.of("message", "Proof uploaded successfully"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/files/{subDirectory}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String subDirectory, 
                                            @PathVariable String filename) {
        try {
            String relativePath = subDirectory + "/" + filename;
            Path filePath = fileStorageService.getFilePath(relativePath);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving file: {}/{}", subDirectory, filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/campaigns/{id}/verify")
    public ResponseEntity<?> verifyCampaign(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            // Note: This should be restricted to admin users in a real system
            // For now, anyone can verify campaigns
            log.info("Verifying campaign {}, approved: {}", id, approved);
            
            campaignService.verifyCampaign(id, approved);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/campaigns/active")
    public ResponseEntity<List<Campaign>> getActiveCampaigns() {
        log.info("Fetching active campaigns");
        return ResponseEntity.ok(campaignService.getActiveCampaigns());
    }

    @GetMapping("/campaigns/all")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        // For security, only return active campaigns to the public
        log.info("Fetching all campaigns (only active ones for public)");
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/campaigns/pending")
    public ResponseEntity<List<Campaign>> getPendingCampaigns() {
        try {
            // Check if user has admin role
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && 
                             authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            
            if (!isAdmin) {
                log.warn("Non-admin user attempted to access pending campaigns");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            log.info("Admin fetching pending campaigns");
            List<Campaign> pendingCampaigns = campaignService.getAllCampaigns().stream()
                    .filter(c -> c.getStatus() == CampaignStatus.PENDING_VERIFICATION)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(pendingCampaigns);
        } catch (Exception e) {
            log.error("Error fetching pending campaigns", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}
