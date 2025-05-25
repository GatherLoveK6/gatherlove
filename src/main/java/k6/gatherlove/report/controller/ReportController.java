package k6.gatherlove.report.controller;

import k6.gatherlove.report.enums.Role;
import k6.gatherlove.report.dto.ReportRequestDTO;
import k6.gatherlove.report.factory.ReportServiceFactory;
import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportServiceFactory reportServiceFactory;

    public ReportController(ReportServiceFactory reportServiceFactory) {
        this.reportServiceFactory = reportServiceFactory;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        Role userRole;
        try {
            userRole = Role.valueOf(role.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role: {}", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        if (userRole == Role.ADMIN) {
            logger.warn("Admin attempted to create a report: {}", userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins are not allowed to create reports.");
        }

        logger.info("User {} is creating a report", userId);
        ReportService action = reportServiceFactory.getReportAction(userRole);
        Report report = action.createReport(
                request.getCampaignId(),
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getViolationType()
        );

        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String springRole = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        Role role;
        try {
            role = Role.valueOf(springRole.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role on report view: {}", springRole);
            return ResponseEntity.badRequest().build();
        }

        logger.info("{} is viewing reports", userId);
        ReportService action = reportServiceFactory.getReportAction(role);
        return (role == Role.ADMIN)
                ? ResponseEntity.ok(action.viewReports(null))
                : ResponseEntity.ok(action.viewReports(userId));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String springRole = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        Role role;
        try {
            role = Role.valueOf(springRole.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role on delete: {}", springRole);
            return ResponseEntity.status(403).build();
        }

        if (role != Role.ADMIN) {
            logger.warn("Unauthorized delete attempt for report {} with role {}", reportId, role);
            return ResponseEntity.status(403).build();
        }

        logger.info("Deleting report {}", reportId);
        ReportService action = reportServiceFactory.getReportAction(role);
        action.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/campaigns/{campaignId}/verify")
    public ResponseEntity<String> verifyCampaign(@PathVariable String campaignId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String springRole = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        Role role;
        try {
            role = Role.valueOf(springRole.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role on verify: {}", springRole);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        if (role != Role.ADMIN) {
            logger.warn("Non-admin tried to verify campaign {}", campaignId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can verify campaigns.");
        }

        logger.info("Verifying campaign {}", campaignId);
        ReportService action = reportServiceFactory.getReportAction(role);
        action.verifyCampaign(campaignId);
        return ResponseEntity.ok("Campaign verified successfully.");
    }
}
