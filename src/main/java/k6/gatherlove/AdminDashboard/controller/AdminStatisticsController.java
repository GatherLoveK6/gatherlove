package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import k6.gatherlove.AdminDashboard.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    @GetMapping
    public ResponseEntity<AdminStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getPlatformStatistics());
    }
}
