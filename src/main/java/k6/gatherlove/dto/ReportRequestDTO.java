package k6.gatherlove.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private String userId;
    private String campaignId;
    private String reason;
    private String evidenceUrl;
}
