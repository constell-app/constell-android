package dev.shoheiyamagiwa.constell.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public sealed class AuthScreenState {
    public object Loading : AuthScreenState()
    public data class SignUp(val displayName: String = "", val email: String = "", val password: String = "", val confirmPassword: String = "") : AuthScreenState()
    public data class SignIn(val email: String = "", val password: String = "") : AuthScreenState()
}

public class AuthViewModel : ViewModel() {
    private val _screenState = MutableStateFlow<AuthScreenState>(value = AuthScreenState.Loading)
    public val screenState = _screenState.asStateFlow()

    /**
     * Validate a login session to restore the previous user data if possible
     */
    public fun validateSession() {
        viewModelScope.launch {
            _screenState.value = AuthScreenState.Loading

            delay(timeMillis = 1000L)  // TODO: Implement actual session restoring logic

            _screenState.value = AuthScreenState.SignIn(email = "", password = "") // For now, just navigate to the sign-in screen
        }
    }

    /**
     * Update the screen to the sign-up screen
     */
    public fun updateToSignUp() {
        when (val currentState = _screenState.value) {
            is AuthScreenState.Loading -> {
                _screenState.value = AuthScreenState.SignIn()
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
                _screenState.value = AuthScreenState.SignIn()
            }

            is AuthScreenState.SignIn -> {
                return
            }

            is AuthScreenState.SignUp -> {
                _screenState.value = AuthScreenState.SignIn(email = currentState.email, password = currentState.password)
            }
        }
    }

    public fun submit() {
        when (_screenState.value) {
            is AuthScreenState.SignUp -> {
                // TODO: Implement sign up functionality
            }

            is AuthScreenState.SignIn -> {
                // TODO: Implement sign in functionality
            }

            else -> throw IllegalStateException("Invalid screen state: ${_screenState.value}")
        }
    }

    /**
     * Update the display name field on the screen
     */
    public fun updateDisplayName(value: String) {
        val currentScreenState = _screenState.value
        if (currentScreenState is AuthScreenState.SignUp) {
            _screenState.value = currentScreenState.copy(displayName = value)
        }
    }

    public fun updateEmail(value: String) {
        val currentFormData = _screenState.value
        if (currentFormData is AuthScreenState.SignUp) {
            _screenState.value = currentFormData.copy(email = value)
        }
        if (currentFormData is AuthScreenState.SignIn) {
            _screenState.value = currentFormData.copy(email = value)
        }
    }

    public fun updatePassword(value: String) {
        val currentFormData = _screenState.value
        if (currentFormData is AuthScreenState.SignUp) {
            _screenState.value = currentFormData.copy(password = value)
        }
        if (currentFormData is AuthScreenState.SignIn) {
            _screenState.value = currentFormData.copy(password = value)
        }
    }
}

public val authViewModelFactory = viewModelFactory {
    initializer {
        AuthViewModel()
    }
}