package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Override
    public void sendAnnouncement(AnnouncementRequest request) {
        logAnnouncement(request);
    }

    private void logAnnouncement(AnnouncementRequest request) {
        System.out.println("Sending announcement: " + request.getTitle() + " - " + request.getMessage());
    }
}