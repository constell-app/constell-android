package dev.shoheiyamagiwa.constell.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public data class ArticleNode(val id: String, val title: String, val tags: List<String>, val description: String, val similarArticles: List<ArticleNode>)

public data class CenterNode(val id: String, val title: String)

public sealed class HomeScreenState {
    object Loading : HomeScreenState()
    class Default(val mainArticleNode: ArticleNode, val showArticleDetails: Boolean) : HomeScreenState()
    class Error(val exception: Exception) : HomeScreenState()
}

public class HomeViewModel : ViewModel() {
    private val _screenState = MutableStateFlow<HomeScreenState>(value = HomeScreenState.Loading)
    public val screenState = _screenState.asStateFlow()

    /**
     * Initialize the home screen state.
     */
    public fun initializeScreen() {
        viewModelScope.launch {
            _screenState.value = HomeScreenState.Loading

            try {
                // FIXME: These are just mock data so we must replace them with actual data
                val article1 = ArticleNode(id = "1", title = "Kotlin", tags = listOf("Backend", "Android"), description = "Kotlin is a programming language.", similarArticles = listOf())
                val article2 = ArticleNode(id = "2", title = "Flutter", tags = listOf("Frontend", "Mobile"), description = "Flutter is a cross-platform mobile application framework.", similarArticles = listOf())
                val article3 = ArticleNode(id = "3", title = "Dart", tags = listOf("Backend", "Web"), description = "Dart is a programming language.", similarArticles = listOf())
                val article4 = ArticleNode(id = "4", title = "Java", tags = listOf("Backend", "Android"), description = "Java is a programming language.", similarArticles = listOf())
                val article5 = ArticleNode(id = "5", title = "JavaScript", tags = listOf("Frontend", "Web"), description = "JavaScript is a programming language.", similarArticles = listOf())
                val mainArticleNode = ArticleNode(id = "0", title = "React", tags = listOf("Frontend", "Web"), description = "React is a Web application framework.", similarArticles = listOf(article1, article2, article3, article4, article5))

                delay(timeMillis = 1000L)

                _screenState.value = HomeScreenState.Default(mainArticleNode = mainArticleNode, showArticleDetails = false)
            } catch (e: Exception) {
                _screenState.value = HomeScreenState.Error(exception = e) // Unreachable code for now
            }
        }
    }
}