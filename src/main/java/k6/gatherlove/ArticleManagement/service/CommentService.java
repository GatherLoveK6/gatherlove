package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.CommentArticle;

import java.util.List;

public interface CommentService {
    CommentArticle addComment(Long articleId, String username, String content);
    List<CommentArticle> getCommentsForArticle(Long articleId);
}