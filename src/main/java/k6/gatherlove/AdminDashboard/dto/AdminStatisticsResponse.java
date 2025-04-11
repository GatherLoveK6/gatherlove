package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatisticsResponse {
    private int totalCampaigns;
    private int totalDonations;
    private int totalUsers;
}