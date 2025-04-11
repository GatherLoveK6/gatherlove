package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnnouncementRequest {
    private String title;
    private String message;
}
