package dev.shoheiyamagiwa.constell.feature.home.data

import dev.shoheiyamagiwa.constell.feature.home.model.Article
import dev.shoheiyamagiwa.constell.feature.home.model.ArticleConnection
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

public class SupabaseArticleRepository(private val supabaseClient: SupabaseClient) :
    ArticleRepository {
    override suspend fun getArticles(userId: String): List<Article> {
        return supabaseClient.from(table = "articles")
            .select {
                filter {
                    eq(column = "user_id", value = userId)
                    eq(column = "status", value = "completed")
                }
            }
            .decodeList<ArticleResponseDto>().map {
                Article(
                    id = it.id,
                    userId = it.userId,
                    title = it.title,
                    tags = it.tags,
                    summary = it.summary,
                    url = it.url
                )
            }
    }

    override suspend fun getArticleConnections(userId: String): List<ArticleConnection> {
        return supabaseClient.from(table = "article_connections")
            .select {
                filter {
                    eq(column = "user_id", value = userId)
                }
            }
            .decodeList<ArticleConnectionResponseDto>().map {
                ArticleConnection(
                    id = it.id,
                    userId = it.userId,
                    sourceArticleId = it.sourceArticleId,
                    targetArticleId = it.targetArticleId,
                    similarityScore = it.similarityScore
                )
            }
    }

    override suspend fun saveArticle(userId: String, url: String) {
        val requestDto = ArticleRequestDto(userId = userId, url = url)

        supabaseClient.from(table = "articles").insert(value = requestDto)
    }
}