package dev.shoheiyamagiwa.constell.feature.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val url: String,
    val title: String,
    val tags: List<String>,
    val summary: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class ArticleConnectionDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("source_article_id")
    val sourceArticleId: String,
    @SerialName("target_article_id")
    val targetArticleId: String,
    @SerialName("similarity_score")
    val similarityScore: Double,
    @SerialName("created_at")
    val createdAt: String
)
