package k6.gatherlove.ArticleManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // or you can use a relation to a User table

    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private ArticleManagementModel article;
}