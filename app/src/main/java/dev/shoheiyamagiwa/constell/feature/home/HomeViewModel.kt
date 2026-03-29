package dev.shoheiyamagiwa.constell.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public data class ArticleNode(
    val id: String,
    val title: String,
    val tags: List<String>,
    val description: String,
    val similarArticles: List<ArticleNode>
)

public sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Default(val mainArticleNode: ArticleNode?, val showArticleDetails: Boolean) : HomeScreenState()
    data class Error(val exception: Exception) : HomeScreenState()
}

public class HomeViewModel(private val articleRepository: ArticleRepository) : ViewModel() {
    private val _screenState = MutableStateFlow<HomeScreenState>(value = HomeScreenState.Loading)
    public val screenState = _screenState.asStateFlow()

    /**
     * Initialize the home screen state.
     */
    public fun initializeScreen() {
        viewModelScope.launch {
            _screenState.value = HomeScreenState.Loading

            try {
                val articles = articleRepository.getArticles()
                val connections = articleRepository.getArticleConnections()

                if (articles.isEmpty()) {
                    _screenState.value = HomeScreenState.Default(mainArticleNode = null, showArticleDetails = false)
                    return@launch
                }

                // FIXME: For now, take the first article as the main node
                val mainArticleDto = articles.first()

                // Find similar articles based on connections
                val similarArticleIds = connections
                    .filter { it.sourceArticleId == mainArticleDto.id }
                    .map { it.targetArticleId }
                    .toSet()

                val similarArticles = articles.filter { it.id in similarArticleIds }.map { dto ->
                    ArticleNode(
                        id = dto.id,
                        title = dto.title,
                        tags = dto.tags,
                        description = dto.summary,
                        similarArticles = emptyList()
                    )
                }

                val mainArticleNode = ArticleNode(
                    id = mainArticleDto.id,
                    title = mainArticleDto.title,
                    tags = mainArticleDto.tags,
                    description = mainArticleDto.summary,
                    similarArticles = similarArticles
                )

                _screenState.value = HomeScreenState.Default(mainArticleNode = mainArticleNode, showArticleDetails = false)
            } catch (e: Exception) {
                _screenState.value = HomeScreenState.Error(exception = e)
            }
        }
    }

    /**
     * Show or hide article details.
     */
    public fun setShowArticleDetails(show: Boolean) {
        val currentState = _screenState.value
        if (currentState is HomeScreenState.Default) {
            _screenState.value = currentState.copy(showArticleDetails = show)
        }
    }
}