package k6.gatherlove.report.controller;

import k6.gatherlove.report.enums.Role;
import k6.gatherlove.report.dto.ReportRequestDTO;
import k6.gatherlove.report.factory.ReportServiceFactory;
import k6.gatherlove.report.model.Report;
import k6.gatherlove.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Typically the username or email
        String role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        Role userRole;
        try {
            userRole = Role.valueOf(role.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        if (userRole == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins are not allowed to create reports.");
        }

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
    // Retrieve the authenticated user's details
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = authentication.getName(); // Typically username or email

    String springRole = authentication.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .findFirst()
            .orElse("ROLE_USER"); // fallback to ROLE_USER

    // Convert ROLE_ADMIN or ROLE_USER to enum Role.ADMIN / Role.USER
    Role role;
    try {
        role = Role.valueOf(springRole.replace("ROLE_", ""));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build(); // Invalid role
    }

    ReportService action = reportServiceFactory.getReportAction(role);

    if (role == Role.ADMIN) {
        return ResponseEntity.ok(action.viewReports(null)); // admin sees all
    } else {
        return ResponseEntity.ok(action.viewReports(userId)); // user sees own
    }
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
        return ResponseEntity.status(403).build();
    }

    if (role != Role.ADMIN) {
        return ResponseEntity.status(403).build(); // Forbidden
    }

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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
    }

    if (role != Role.ADMIN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can verify campaigns.");
    }

    ReportService action = reportServiceFactory.getReportAction(role);
    action.verifyCampaign(campaignId);
    return ResponseEntity.ok("Campaign verified successfully.");
    }

}
