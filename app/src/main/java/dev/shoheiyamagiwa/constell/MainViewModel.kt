package dev.shoheiyamagiwa.constell

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

public sealed class MainUiState {
    public object Loading : MainUiState()
    public data class Success(val isLoggedIn: Boolean, val isFirstLaunch: Boolean) : MainUiState()
}

public class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(value = MainUiState.Loading)
    public val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                flow = userPreferencesRepository.isFirstLaunch,
                flow2 = userPreferencesRepository.isLoggedIn
            ) { isFirstLaunch, isLoggedIn ->
                MainUiState.Success(isLoggedIn = isLoggedIn, isFirstLaunch = isFirstLaunch)
            }.collect {
                _uiState.value = it
            }
        }
    }

    public fun updateLoggedIn(isLoggedIn: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateLoggedIn(isLoggedIn = isLoggedIn)
        }
    }

    public fun updateFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateFirstLaunch(isFirstLaunch = isFirstLaunch)
        }
    }

    public fun saveSharedArticle(articleUrl: String) {
        viewModelScope.launch {
            try {
                val userId = userPreferencesRepository.userId.first()
                if (userId.isNotBlank()) {
                    articleRepository.saveArticle(userId = userId, url = articleUrl)
                }
            } catch (e: Exception) {
                // Just logging for now
                Log.e("MainViewModel", "Failed to save shared article: $e")
            }
        }
    }
}
