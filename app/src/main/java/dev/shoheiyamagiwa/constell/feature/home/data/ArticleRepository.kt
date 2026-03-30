package dev.shoheiyamagiwa.constell.feature.home.data

public interface ArticleRepository {
    suspend fun getArticles(): List<ArticleDto>
    suspend fun getArticleConnections(): List<ArticleConnectionDto>
}