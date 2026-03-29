package dev.shoheiyamagiwa.constell.feature.home.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public class SupabaseArticleRepository(private val supabaseClient: SupabaseClient) : ArticleRepository {
    override suspend fun getArticles(): List<ArticleDto> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["articles"]
            .select()
            .decodeList<ArticleDto>()
    }

    override suspend fun getArticleConnections(): List<ArticleConnectionDto> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["article_connections"]
            .select()
            .decodeList<ArticleConnectionDto>()
    }
}