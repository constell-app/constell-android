package dev.shoheiyamagiwa.constell.feature.home.model

public data class Article(
    val id: String,
    val userId: String,
    val url: String,
    val title: String,
    val tags: List<String>,
    val summary: String
)

public data class ArticleConnection(
    val id: String,
    val userId: String,
    val sourceArticleId: String,
    val targetArticleId: String,
    val similarityScore: Double
)