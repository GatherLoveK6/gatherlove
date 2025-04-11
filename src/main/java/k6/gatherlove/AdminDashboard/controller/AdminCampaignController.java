package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.service.AdminCampaignService;
import k6.gatherlove.AdminDashboard.dto.Campaign;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import k6.gatherlove.AdminDashboard.dto.StatusUpdateRequest;


@RestController
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {

    private final AdminCampaignService adminCampaignService;

    @GetMapping
    public ResponseEntity<List<Campaign>> getCampaignsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(adminCampaignService.getCampaignsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Campaign> updateCampaignStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        Campaign updated = adminCampaignService.updateCampaignStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

}
