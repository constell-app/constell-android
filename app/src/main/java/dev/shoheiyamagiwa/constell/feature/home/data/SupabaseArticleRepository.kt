package dev.shoheiyamagiwa.constell.feature.home.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

public class SupabaseArticleRepository(private val supabaseClient: SupabaseClient) : ArticleRepository {
    override suspend fun getArticles(): List<ArticleDto> {
        return supabaseClient.postgrest["articles"]
            .select {
                filter {
                    eq(column = "status", value = "completed")
                }
            }
            .decodeList<ArticleDto>()
    }

    override suspend fun getArticleConnections(): List<ArticleConnectionDto> {
        return supabaseClient.postgrest["article_connections"]
            .select()
            .decodeList<ArticleConnectionDto>()
    }
}