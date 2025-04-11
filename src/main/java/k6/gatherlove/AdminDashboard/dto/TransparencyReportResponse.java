package k6.gatherlove.AdminDashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransparencyReportResponse {
    private Long campaignId;
    private int totalReceived;
    private int totalUsed;
    private List<String> usageBreakdown;
}