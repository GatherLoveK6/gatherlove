package k6.gatherlove.donation.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentRequest {
    private String userId;
    private String text;
}
