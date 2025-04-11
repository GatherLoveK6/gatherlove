package k6.gatherlove.ArticleManagement.controller;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;
import k6.gatherlove.ArticleManagement.service.ArticleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleManagementController {

    private final ArticleManagementService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleManagementModel>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PostMapping
    public ResponseEntity<ArticleManagementModel> createArticle(@RequestBody ArticleManagementModel article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }
}
