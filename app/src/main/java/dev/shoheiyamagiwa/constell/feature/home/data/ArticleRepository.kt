package dev.shoheiyamagiwa.constell.feature.home.data

import dev.shoheiyamagiwa.constell.feature.home.model.Article
import dev.shoheiyamagiwa.constell.feature.home.model.ArticleConnection

public interface ArticleRepository {
    public suspend fun getArticles(userId: String): List<Article>
    public suspend fun getArticleConnections(userId: String): List<ArticleConnection>
    public suspend fun saveArticle(userId: String, url: String)
}