package k6.gatherlove.AdminDashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    private Long id;
    private String title;
    private String status;
}
