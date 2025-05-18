package k6.gatherlove.controller;

import k6.gatherlove.dto.ReportRequestDTO;
import k6.gatherlove.factory.ReportServiceFactory;
import k6.gatherlove.model.Report;
import k6.gatherlove.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportServiceFactory reportServiceFactory;

    public ReportController(ReportServiceFactory reportServiceFactory) {
        this.reportServiceFactory = reportServiceFactory;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequestDTO request) {
        ReportService action = reportServiceFactory.getReportAction("USER");
        Report report = action.createReport(
                request.getCampaignId(),
                request.getUserId(),
                request.getTitle(),
                request.getDescription(),
                request.getViolationType()

        );
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getReports(@RequestParam("role") String role,
                                                   @RequestParam(value = "userId", required = false) String userId) {
        ReportService action = reportServiceFactory.getReportAction(role);

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(action.viewReports(null)); // ignored inside admin implementation
        } else if (userId != null) {
            return ResponseEntity.ok(action.viewReports(userId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId,
                                             @RequestParam("role") String role) {
        ReportService action = reportServiceFactory.getReportAction(role);
        action.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/campaigns/{campaignId}/verify")
    public ResponseEntity<String> verifyCampaign(@PathVariable String campaignId,
                                                 @RequestParam("role") String role) {
        ReportService action = reportServiceFactory.getReportAction(role);
        action.verifyCampaign(campaignId);
        return ResponseEntity.ok("Campaign verified successfully.");
    }
}

