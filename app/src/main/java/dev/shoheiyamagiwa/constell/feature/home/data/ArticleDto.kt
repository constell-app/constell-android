package dev.shoheiyamagiwa.constell.feature.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ArticleRequestDto(
    @SerialName(value = "user_id")
    val userId: String,
    val url: String
)

@Serializable
public data class ArticleResponseDto(
    val id: String,
    @SerialName(value = "user_id")
    val userId: String,
    val url: String,
    val title: String,
    val tags: List<String>,
    val summary: String,
    val status: String,
    @SerialName(value = "created_at")
    val createdAt: String
)

@Serializable
public data class ArticleConnectionResponseDto(
    val id: String,
    @SerialName(value = "user_id")
    val userId: String,
    @SerialName(value = "source_article_id")
    val sourceArticleId: String,
    @SerialName(value = "target_article_id")
    val targetArticleId: String,
    @SerialName(value = "similarity_score")
    val similarityScore: Double,
    @SerialName(value = "created_at")
    val createdAt: String
)
