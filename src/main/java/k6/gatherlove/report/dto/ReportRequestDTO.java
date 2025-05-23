package k6.gatherlove.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    
    private String campaignId;
    private String title;
    private String description;
    private String violationType;


}
