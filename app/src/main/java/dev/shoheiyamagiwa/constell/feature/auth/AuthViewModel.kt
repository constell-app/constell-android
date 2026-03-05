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

            delay(timeMillis = 2000L)  // TODO: Implement actual session restoring logic

            _screenState.value = AuthScreenState.SignIn() // For now, just navigate to the sign-in screen
        }
    }

    /**
     * Update the screen to the sign-up screen
     */
    public fun updateToSignUp(displayName: String = "", email: String = "", password: String = "", confirmPassword: String = "") {
        _screenState.value = AuthScreenState.SignUp(displayName, email, password, confirmPassword)
    }

    /**
     * Update the screen to the sign-in screen
     */
    public fun updateToSignIn(email: String = "", password: String = "") {
        _screenState.value = AuthScreenState.SignIn(email, password)
    }
}

public val authViewModelFactory = viewModelFactory {
    initializer {
        AuthViewModel()
    }
}