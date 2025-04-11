package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;

public interface AdminAnnouncementService {
    void sendAnnouncement(AnnouncementRequest request);
}