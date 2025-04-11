package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import k6.gatherlove.AdminDashboard.service.AdminAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AdminAnnouncementService adminAnnouncementService;

    @PostMapping
    public ResponseEntity<Void> sendAnnouncement(@RequestBody AnnouncementRequest request) {
        adminAnnouncementService.sendAnnouncement(request);
        return ResponseEntity.ok().build();
    }
}
