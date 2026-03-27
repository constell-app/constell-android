package dev.shoheiyamagiwa.constell.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.WithEmail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public sealed class AuthUiState {
    public object Loading : AuthUiState()
    public data class SignUp(val displayName: String = "", val email: String = "", val password: String = "", val confirmPassword: String = "") : AuthUiState()
    public data class SignIn(val email: String = "", val password: String = "") : AuthUiState()
}

public sealed interface AuthUiEvent {
    public data object NavigateToHome : AuthUiEvent
    public data class NavigateToConfirmEmail(val email: String) : AuthUiEvent
}

public class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _screenState = MutableStateFlow<AuthUiState>(value = AuthUiState.Loading)
    public val screenState = _screenState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    public val uiEvent = _uiEvent.asSharedFlow()

    /**
     * Validate a login session to restore the previous user data if possible
     */
    public fun validateSession() {
        viewModelScope.launch {
            _screenState.value = AuthUiState.Loading

            if (authRepository.isAuthenticated()) {
                authRepository.refreshSession()

                _uiEvent.emit(value = AuthUiEvent.NavigateToHome)
                return@launch
            } else {
                _screenState.value = AuthUiState.SignIn(email = "", password = "")
            }
        }
    }

    /**
     * Update the screen to the sign-up screen
     */
    public fun updateToSignUp() {
        when (val currentState = _screenState.value) {
            is AuthUiState.Loading -> {
                return
            }

            is AuthUiState.SignIn -> {
                _screenState.value = AuthUiState.SignUp(email = currentState.email, password = currentState.password)
            }

            is AuthUiState.SignUp -> {
                return
            }
        }
    }

    /**
     * Update the screen to the sign-in screen
     */
    public fun updateToSignIn() {
        when (val currentState = _screenState.value) {
            is AuthUiState.Loading -> {
                return
            }

            is AuthUiState.SignIn -> {
                return
            }

            is AuthUiState.SignUp -> {
                _screenState.value = AuthUiState.SignIn(email = currentState.email, password = currentState.password)
            }
        }
    }

    public fun submit() {
        when (val state = _screenState.value) {
            is AuthUiState.SignUp -> {
                viewModelScope.launch {
                    try {
                        if (state.password != state.confirmPassword) {
                            throw Exception("Passwords do not match")
                        }

                        if (authRepository !is WithEmail) {
                            throw Exception("Email authentication is not supported")
                        }

                        authRepository.signUpWithEmail(state.email, state.password)

                        _uiEvent.emit(value = AuthUiEvent.NavigateToConfirmEmail(state.email))
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }
            }

            is AuthUiState.SignIn -> {
                viewModelScope.launch {
                    try {
                        if (authRepository !is WithEmail) {
                            throw Exception("Email authentication is not supported")
                        }

                        authRepository.signInWithEmail(state.email, state.password)

                        _uiEvent.emit(value = AuthUiEvent.NavigateToHome)
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }
            }

            else -> throw IllegalStateException("Invalid screen state: ${_screenState.value}")
        }
    }

    /**
     * Update the display name field on the screen
     */
    public fun updateDisplayName(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthUiState.SignUp) {
            _screenState.value = currentState.copy(displayName = value)
        }
    }

    public fun updateEmail(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthUiState.SignUp) {
            _screenState.value = currentState.copy(email = value)
        }
        if (currentState is AuthUiState.SignIn) {
            _screenState.value = currentState.copy(email = value)
        }
    }

    public fun updatePassword(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthUiState.SignUp) {
            _screenState.value = currentState.copy(password = value)
        }
        if (currentState is AuthUiState.SignIn) {
            _screenState.value = currentState.copy(password = value)
        }
    }

    public fun updateConfirmPassword(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthUiState.SignUp) {
            _screenState.value = currentState.copy(confirmPassword = value)
        }
    }
}