package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonationHistoryResponse {
    private String userId;
    private double amount;
}
