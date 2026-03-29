package dev.shoheiyamagiwa.constell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

public sealed class MainUiState {
    public object Loading : MainUiState()
    public data class Success(val isLoggedIn: Boolean, val isFirstLaunch: Boolean) : MainUiState()
}

public class MainViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(value = MainUiState.Loading)
    public val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(flow = repository.isFirstLaunch, flow2 = repository.isLoggedIn) { isFirstLaunch, isLoggedIn ->
                MainUiState.Success(isLoggedIn = isLoggedIn, isFirstLaunch = isFirstLaunch)
            }.collect {
                _uiState.value = it
            }
        }
    }
}
