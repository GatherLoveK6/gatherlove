package k6.gatherlove.ArticleManagement.controller;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.model.CommentArticle;
import k6.gatherlove.ArticleManagement.service.ArticleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import k6.gatherlove.ArticleManagement.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleManagementController {

    private final ArticleManagementService articleService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<ArticleManagementModel>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PostMapping
    public ResponseEntity<ArticleManagementModel> createArticle(@RequestBody ArticleManagementModel article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleManagementModel> updateArticle(@PathVariable Long id, @RequestBody ArticleManagementModel updated) {
        return ResponseEntity.ok(articleService.updateArticle(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleManagementModel> likeArticle(@PathVariable Long id) {
        ArticleManagementModel article = articleService.getArticleById(id);
        article.setLikes(article.getLikes() + 1);
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentArticle> addComment(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String content) {
        return ResponseEntity.ok(commentService.addComment(id, username, content)); // ✅ fix
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentArticle>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsForArticle(id)); // ✅ fix
    }

}