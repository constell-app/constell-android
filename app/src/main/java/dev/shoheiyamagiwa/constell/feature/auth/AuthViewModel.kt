package dev.shoheiyamagiwa.constell.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.WithEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public sealed class AuthScreenState {
    public object Loading : AuthScreenState()
    public data class SignUp(val displayName: String = "", val email: String = "", val password: String = "", val confirmPassword: String = "") : AuthScreenState()
    public data class SignIn(val email: String = "", val password: String = "") : AuthScreenState()
}

public class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _screenState = MutableStateFlow<AuthScreenState>(value = AuthScreenState.Loading)
    public val screenState = _screenState.asStateFlow()

    /**
     * Validate a login session to restore the previous user data if possible
     */
    public fun validateSession() {
        viewModelScope.launch {
            _screenState.value = AuthScreenState.Loading

            if (authRepository.isAuthenticated()) {
                authRepository.refreshSession()
                // TODO: Implement screen transition
                return@launch
            } else {
                _screenState.value = AuthScreenState.SignIn(email = "", password = "")
            }
        }
    }

    /**
     * Update the screen to the sign-up screen
     */
    public fun updateToSignUp() {
        when (val currentState = _screenState.value) {
            is AuthScreenState.Loading -> {
                return
            }

            is AuthScreenState.SignIn -> {
                _screenState.value = AuthScreenState.SignUp(email = currentState.email, password = currentState.password)
            }

            is AuthScreenState.SignUp -> {
                return
            }
        }
    }

    /**
     * Update the screen to the sign-in screen
     */
    public fun updateToSignIn() {
        when (val currentState = _screenState.value) {
            is AuthScreenState.Loading -> {
                return
            }

            is AuthScreenState.SignIn -> {
                return
            }

            is AuthScreenState.SignUp -> {
                _screenState.value = AuthScreenState.SignIn(email = currentState.email, password = currentState.password)
            }
        }
    }

    public fun submit(onSuccess: () -> Unit = {}) {
        when (val state = _screenState.value) {
            is AuthScreenState.SignUp -> {
                viewModelScope.launch {
                    try {
                        if (state.password != state.confirmPassword) {
                            throw Exception("Passwords do not match")
                        }

                        if (authRepository !is WithEmail) {
                            throw Exception("Email authentication is not supported")
                        }

                        authRepository.signUpWithEmail(state.email, state.password)
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }

                onSuccess()
            }

            is AuthScreenState.SignIn -> {
                viewModelScope.launch {
                    try {
                        if (authRepository !is WithEmail) {
                            throw Exception("Email authentication is not supported")
                        }

                        authRepository.signInWithEmail(state.email, state.password)
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }

                onSuccess()
            }

            else -> throw IllegalStateException("Invalid screen state: ${_screenState.value}")
        }
    }

    /**
     * Update the display name field on the screen
     */
    public fun updateDisplayName(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthScreenState.SignUp) {
            _screenState.value = currentState.copy(displayName = value)
        }
    }

    public fun updateEmail(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthScreenState.SignUp) {
            _screenState.value = currentState.copy(email = value)
        }
        if (currentState is AuthScreenState.SignIn) {
            _screenState.value = currentState.copy(email = value)
        }
    }

    public fun updatePassword(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthScreenState.SignUp) {
            _screenState.value = currentState.copy(password = value)
        }
        if (currentState is AuthScreenState.SignIn) {
            _screenState.value = currentState.copy(password = value)
        }
    }

    public fun updateConfirmPassword(value: String) {
        val currentState = _screenState.value
        if (currentState is AuthScreenState.SignUp) {
            _screenState.value = currentState.copy(confirmPassword = value)
        }
    }
}