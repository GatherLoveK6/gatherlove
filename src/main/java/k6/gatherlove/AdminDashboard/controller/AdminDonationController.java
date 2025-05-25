package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import k6.gatherlove.AdminDashboard.service.AdminDonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/donations")
@RequiredArgsConstructor
public class AdminDonationController {

    private final AdminDonationService adminDonationService;

    @GetMapping("/history")
    public ResponseEntity<List<DonationHistoryResponse>> getDonationHistory(@RequestParam String campaignId) {
        return ResponseEntity.ok(adminDonationService.getDonationHistoryByCampaignId(campaignId));
    }
}