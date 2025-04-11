package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AnnouncementRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Override
    public void sendAnnouncement(AnnouncementRequest request) {
        // Mock logic — in real case, you'd push this to notification service or DB
        System.out.println("Sending announcement: " + request.getTitle() + " - " + request.getMessage());
    }
}