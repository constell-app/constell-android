package dev.shoheiyamagiwa.constell.feature.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.WithEmail
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

public sealed class FormField(val value: String = "", @param:StringRes open val errorMessageResId: Int? = null) {
    /**
     * Validate the value of the form field and return a new instance of the form field with the updated value and error message
     */
    public abstract fun validate(value: String): FormField

    /**
     * Check if the form field has an error
     *
     * @return true if the form field has an error, false otherwise
     */
    public fun hasError(): Boolean {
        return errorMessageResId != null
    }

    public class EmailField(value: String = "", @StringRes errorMessageResId: Int? = null) : FormField(value, errorMessageResId) {
        override fun validate(value: String): EmailField {
            val error = when {
                value.isEmpty() -> R.string.auth_error_email_empty
                !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> R.string.auth_error_email_invalid
                else -> null
            }
            return EmailField(value = value, errorMessageResId = error)
        }
    }

    public class PasswordField(value: String = "", @StringRes errorMessageResId: Int? = null) : FormField(value, errorMessageResId) {
        override fun validate(value: String): PasswordField {
            val error = when {
                value.isEmpty() -> R.string.auth_error_password_empty
                value.length < 8 -> R.string.auth_error_password_too_short
                else -> null
            }
            return PasswordField(value = value, errorMessageResId = error)
        }
    }

    public class ConfirmPasswordField(value: String = "", @StringRes errorMessageResId: Int? = null) : FormField(value, errorMessageResId) {
        override fun validate(value: String): ConfirmPasswordField {
            // ConfirmPasswordField requires comparison with the original password,
            // which should be handled at the ViewModel or a specialized validate method.
            // For now, we only check for empty value here.
            val error = when {
                value.isEmpty() -> R.string.auth_error_password_empty
                else -> null
            }
            return ConfirmPasswordField(value = value, errorMessageResId = error)
        }

        public fun validate(value: String, passwordValue: String): ConfirmPasswordField {
            val error = when {
                value.isEmpty() -> R.string.auth_error_password_empty
                value != passwordValue -> R.string.auth_error_confirm_password_mismatch
                else -> null
            }
            return ConfirmPasswordField(value = value, errorMessageResId = error)
        }
    }

    public class DisplayNameField(value: String = "", @StringRes errorMessageResId: Int? = null) : FormField(value, errorMessageResId) {
        override fun validate(value: String): DisplayNameField {
            val error = when {
                value.isEmpty() -> R.string.auth_error_display_name_empty
                else -> null
            }
            return DisplayNameField(value = value, errorMessageResId = error)
        }
    }
}

public sealed class AuthUiState {
    public object Loading : AuthUiState()
    public data class SignUp(
        val displayName: FormField.DisplayNameField = FormField.DisplayNameField(),
        val email: FormField.EmailField = FormField.EmailField(),
        val password: FormField.PasswordField = FormField.PasswordField(),
        val confirmPassword: FormField.ConfirmPasswordField = FormField.ConfirmPasswordField()
    ) : AuthUiState()

    public data class SignIn(
        val email: FormField.EmailField = FormField.EmailField(),
        val password: FormField.PasswordField = FormField.PasswordField()
    ) : AuthUiState()
}

public sealed interface AuthUiEvent {
    public data object NavigateToHome : AuthUiEvent
    public data class NavigateToConfirmEmail(val email: String) : AuthUiEvent
    public data object PasswordResetEmailSent : AuthUiEvent // TODO: Implement password reset feature and use this
}

public class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(value = AuthUiState.Loading)
    public val uiState = _uiState.asStateFlow()

    private val _uiEventChannel = Channel<AuthUiEvent>()
    public val uiEvent = _uiEventChannel.receiveAsFlow()

    /**
     * Validate a login session to restore the previous user data if possible
     */
    public fun validateSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            if (authRepository.isAuthenticated()) {
                authRepository.refreshSession()

                _uiEventChannel.send(element = AuthUiEvent.NavigateToHome)
                return@launch
            } else {
                _uiState.value = AuthUiState.SignIn()
            }
        }
    }

    /**
     * Update the screen to the sign-up screen
     */
    public fun changeToSignUpUi() {
        when (val currentState = _uiState.value) {
            is AuthUiState.Loading -> {
                return
            }

            is AuthUiState.SignIn -> {
                _uiState.value = AuthUiState.SignUp(
                    email = currentState.email,
                    password = currentState.password
                )
            }

            is AuthUiState.SignUp -> {
                return
            }
        }
    }

    /**
     * Update the screen to the sign-in screen
     */
    public fun changeToSignInUi() {
        when (val currentState = _uiState.value) {
            is AuthUiState.Loading -> {
                return
            }

            is AuthUiState.SignIn -> {
                return
            }

            is AuthUiState.SignUp -> {
                _uiState.value = AuthUiState.SignIn(
                    email = currentState.email,
                    password = currentState.password
                )
            }
        }
    }

    public fun submit() {
        when (val state = _uiState.value) {
            is AuthUiState.SignUp -> {
                viewModelScope.launch {
                    try {
                        if (state.password.value != state.confirmPassword.value) {
                            throw AuthException.PasswordMismatch()
                        }

                        if (authRepository !is WithEmail) {
                            throw AuthException.EmailAuthNotSupported()
                        }

                        authRepository.signUpWithEmail(state.email.value, state.password.value)
                        // TODO: Set displayName property on the DB

                        _uiEventChannel.send(element = AuthUiEvent.NavigateToConfirmEmail(state.email.value))
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }
            }

            is AuthUiState.SignIn -> {
                viewModelScope.launch {
                    try {
                        if (authRepository !is WithEmail) {
                            throw AuthException.EmailAuthNotSupported()
                        }

                        authRepository.signInWithEmail(state.email.value, state.password.value)

                        _uiEventChannel.send(element = AuthUiEvent.NavigateToHome)
                    } catch (e: Exception) {
                        // TODO: Handle error
                    }
                }
            }

            else -> {
                throw IllegalStateException("Invalid screen state: ${_uiState.value}")
            }
        }
    }

    public fun updateDisplayName(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(displayName = currentState.displayName.validate(value))
        }
    }

    public fun updateEmail(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(email = currentState.email.validate(value))
        }
        if (currentState is AuthUiState.SignIn) {
            _uiState.value = currentState.copy(email = currentState.email.validate(value))
        }
    }

    public fun updatePassword(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            val updatedPassword = currentState.password.validate(value)
            val updatedConfirmPassword = currentState.confirmPassword.validate(currentState.confirmPassword.value, value)
            _uiState.value = currentState.copy(
                password = updatedPassword,
                confirmPassword = updatedConfirmPassword
            )
        }
        if (currentState is AuthUiState.SignIn) {
            _uiState.value = currentState.copy(password = currentState.password.validate(value))
        }
    }

    public fun updateConfirmPassword(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(
                confirmPassword = currentState.confirmPassword.validate(value, currentState.password.value)
            )
        }
    }
}