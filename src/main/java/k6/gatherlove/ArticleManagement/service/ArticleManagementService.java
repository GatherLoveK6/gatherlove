package k6.gatherlove.ArticleManagement.service;

import k6.gatherlove.ArticleManagement.model.ArticleManagementModel;

import java.util.List;

public interface ArticleManagementService {

    ArticleManagementModel createArticle(ArticleManagementModel article);

    ArticleManagementModel getArticleById(Long id);

    List<ArticleManagementModel> getAllArticles();

    ArticleManagementModel updateArticle(Long id, ArticleManagementModel updatedArticle);

    void deleteArticle(Long id);
}
