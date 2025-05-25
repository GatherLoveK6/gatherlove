package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatisticsResponse {
    private long totalCampaigns;
    private long totalDonations;
    private long totalUsers;
}
