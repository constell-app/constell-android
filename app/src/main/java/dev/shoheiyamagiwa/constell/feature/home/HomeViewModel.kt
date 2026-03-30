package dev.shoheiyamagiwa.constell.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleConnectionDto
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleDto
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleRepository
import kotlinx.coroutines.CancellationException
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

public sealed interface HomeAction {
    public data class SelectArticle(val id: String) : HomeAction
    public data class SetShowDetails(val show: Boolean) : HomeAction
    public object Initialize : HomeAction
}

public class HomeViewModel(private val articleRepository: ArticleRepository) : ViewModel() {
    private val _screenState = MutableStateFlow<HomeScreenState>(value = HomeScreenState.Loading)
    public val screenState = _screenState.asStateFlow()

    private var cachedArticles: List<ArticleDto> = emptyList()
    private var cachedConnections: List<ArticleConnectionDto> = emptyList()

    /**
     * Initialize the home screen state.
     */
    public fun initializeScreen() {
        viewModelScope.launch {
            _screenState.value = HomeScreenState.Loading

            try {
                cachedArticles = articleRepository.getArticles()
                cachedConnections = articleRepository.getArticleConnections()

                if (cachedArticles.isEmpty()) {
                    _screenState.value = HomeScreenState.Default(mainArticleNode = null, showArticleDetails = false)
                    return@launch
                }
                // FIXME: For now, take the first article as the main node
                val mainArticleDto = cachedArticles.first()

                updateMainArticle(articleId = mainArticleDto.id, showArticleDetails = false)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }

                _screenState.value = HomeScreenState.Error(exception = e)
            } finally {
                if (_screenState.value is HomeScreenState.Loading) {
                    _screenState.value = HomeScreenState.Default(mainArticleNode = null, showArticleDetails = false)
                }
            }
        }
    }

    /**
     * Select a specific article to be the main node.
     */
    public fun selectArticleById(articleId: String) {
        updateMainArticle(articleId, showArticleDetails = true)
    }

    private fun updateMainArticle(articleId: String, showArticleDetails: Boolean) {
        val mainArticleDto = cachedArticles.find { it.id == articleId } ?: return

        // Find similar articles based on connections (bidirectional)
        val similarArticleIds = cachedConnections
            .filter { it.sourceArticleId == mainArticleDto.id || it.targetArticleId == mainArticleDto.id }
            .map { if (it.sourceArticleId == mainArticleDto.id) it.targetArticleId else it.sourceArticleId }
            .toSet()

        val similarArticles = cachedArticles.filter { it.id in similarArticleIds }.map { dto ->
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

        _screenState.value = HomeScreenState.Default(
            mainArticleNode = mainArticleNode,
            showArticleDetails = showArticleDetails
        )
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