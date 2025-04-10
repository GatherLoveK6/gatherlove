package k6.gatherlove.ArticleManagement.model;

import k6.gatherlove.user.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleManagementModel {
    private Long id;
    private String title;
    private String content;
    private User author;
}
