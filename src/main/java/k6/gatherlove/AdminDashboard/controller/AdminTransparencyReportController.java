package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.TransparencyReportResponse;
import k6.gatherlove.AdminDashboard.service.AdminTransparencyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminTransparencyReportController {

    private final AdminTransparencyReportService adminTransparencyReportService;

    @GetMapping("/{id}/report")
    public ResponseEntity<TransparencyReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(adminTransparencyReportService.getReportByCampaignId(id));
    }
}