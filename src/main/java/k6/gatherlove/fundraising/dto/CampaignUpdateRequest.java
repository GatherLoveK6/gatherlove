package k6.gatherlove.fundraising.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CampaignUpdateRequest {
    private String title;
    private String description;
    private BigDecimal goalAmount;
    private LocalDate deadline;
}