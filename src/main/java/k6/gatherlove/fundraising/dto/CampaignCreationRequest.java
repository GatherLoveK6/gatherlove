package k6.gatherlove.fundraising.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignCreationRequest {
    private String title;
    private String description;
    private BigDecimal goalAmount;
    private LocalDate deadline;
}
