package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonationHistoryResponse {
    private Long id;
    private String donorName;
    private String campaignTitle;
    private int amount;
    private String timestamp;
}