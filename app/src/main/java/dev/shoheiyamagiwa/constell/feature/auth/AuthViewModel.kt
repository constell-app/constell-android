package dev.shoheiyamagiwa.constell.feature.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.WithEmail
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

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
        val confirmPassword: FormField.ConfirmPasswordField = FormField.ConfirmPasswordField(),
        @param:StringRes val errorResId: Int? = null,
    ) : AuthUiState()

    public data class SignIn(
        val email: FormField.EmailField = FormField.EmailField(),
        val password: FormField.PasswordField = FormField.PasswordField(),
        @param:StringRes val errorResId: Int? = null,
    ) : AuthUiState()

    public data class ForgotPassword(
        val email: FormField.EmailField = FormField.EmailField(),
        @param:StringRes val errorResId: Int? = null,
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

    private val _uiEventSharedFlow = MutableSharedFlow<AuthUiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    public val uiEvent = _uiEventSharedFlow.asSharedFlow()

    /**
     * Validate a login session to restore the previous user data if possible
     */
    public fun validateSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                if (authRepository.isAuthenticated()) {
                    authRepository.refreshSession()

                    _uiEventSharedFlow.emit(value = AuthUiEvent.NavigateToHome)
                    return@launch
                } else {
                    _uiState.value = AuthUiState.SignIn()
                }
            } catch (_: Exception) {
                _uiState.value = AuthUiState.SignIn() // Fail-safe
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

            is AuthUiState.ForgotPassword -> {
                _uiState.value = AuthUiState.SignUp(
                    email = currentState.email
                )
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

            is AuthUiState.ForgotPassword -> {
                _uiState.value = AuthUiState.SignIn(
                    email = currentState.email
                )
            }
        }
    }

    /**
     * Update the screen to the forgot password screen
     */
    public fun changeToForgotPasswordUi() {
        when (val currentState = _uiState.value) {
            is AuthUiState.Loading -> {
                return
            }

            is AuthUiState.SignIn -> {
                _uiState.value = AuthUiState.ForgotPassword(
                    email = currentState.email
                )
            }

            is AuthUiState.SignUp -> {
                _uiState.value = AuthUiState.ForgotPassword(
                    email = currentState.email
                )
            }

            is AuthUiState.ForgotPassword -> {
                return
            }
        }
    }

    public fun submit() {
        when (val state = _uiState.value) {
            is AuthUiState.SignUp -> {
                submitSignUp(state)
            }

            is AuthUiState.SignIn -> {
                submitSignIn(state)
            }

            is AuthUiState.ForgotPassword -> {
                submitForgotPassword(state)
            }

            else -> {
                throw IllegalStateException("Invalid screen state: ${_uiState.value}")
            }
        }
    }

    private fun submitSignUp(state: AuthUiState.SignUp) {
        val validatedDisplayName = state.displayName.validate(state.displayName.value)
        val validatedEmail = state.email.validate(state.email.value)
        val validatedPassword = state.password.validate(state.password.value)
        val validatedConfirmPassword = state.confirmPassword.validate(state.confirmPassword.value, passwordValue = state.password.value)

        if (validatedDisplayName.hasError() || validatedEmail.hasError() || validatedPassword.hasError() || validatedConfirmPassword.hasError()) {
            _uiState.value = state.copy(
                displayName = validatedDisplayName,
                email = validatedEmail,
                password = validatedPassword,
                confirmPassword = validatedConfirmPassword,
                errorResId = null
            )
            return
        }

        viewModelScope.launch {
            try {
                if (authRepository !is WithEmail) {
                    throw AuthException.EmailAuthNotSupported()
                }

                authRepository.signUpWithEmail(state.email.value, password = state.password.value)
                // TODO: Set displayName property on the DB

                _uiEventSharedFlow.emit(value = AuthUiEvent.NavigateToConfirmEmail(state.email.value))
            } catch (e: Exception) {
                when (val currentState = uiState.value) {
                    is AuthUiState.SignUp -> {
                        _uiState.value = currentState.copy(errorResId = handleError(e))
                    }

                    else -> {
                        _uiState.value = state.copy(errorResId = handleError(e))
                    }
                }
            }
        }
    }

    private fun submitSignIn(state: AuthUiState.SignIn) {
        val validatedEmail = state.email.validate(state.email.value)
        val validatedPassword = state.password.validate(state.password.value)

        if (validatedEmail.hasError() || validatedPassword.hasError()) {
            _uiState.value = state.copy(
                email = validatedEmail,
                password = validatedPassword,
                errorResId = null
            )
            return
        }

        viewModelScope.launch {
            try {
                if (authRepository !is WithEmail) {
                    throw AuthException.EmailAuthNotSupported()
                }

                authRepository.signInWithEmail(email = state.email.value, password = state.password.value)

                _uiEventSharedFlow.emit(value = AuthUiEvent.NavigateToHome)
            } catch (e: Exception) {
                when (val currentState = uiState.value) {
                    is AuthUiState.SignIn -> {
                        _uiState.value = currentState.copy(errorResId = handleError(e))
                    }

                    else -> {
                        _uiState.value = state.copy(errorResId = handleError(e))
                    }
                }
            }
        }
    }

    private fun submitForgotPassword(state: AuthUiState.ForgotPassword) {
        val validatedEmail = state.email.validate(state.email.value)

        if (validatedEmail.hasError()) {
            _uiState.value = state.copy(
                email = validatedEmail,
                errorResId = null
            )
            return
        }

        viewModelScope.launch {
            try {
                if (authRepository !is WithEmail) {
                    throw AuthException.EmailAuthNotSupported()
                }

                authRepository.requestPasswordReset(email = state.email.value)

                _uiEventSharedFlow.emit(value = AuthUiEvent.PasswordResetEmailSent)
            } catch (e: Exception) {
                when (val currentState = uiState.value) {
                    is AuthUiState.ForgotPassword -> {
                        _uiState.value = currentState.copy(errorResId = handleError(e))
                    }

                    else -> {
                        _uiState.value = state.copy(errorResId = handleError(e))
                    }
                }
            }
        }
    }

    private fun handleError(e: Exception): Int {
        return when (e) {
            is AuthException.EmailAuthNotSupported -> R.string.auth_error_email_not_supported
            is AuthException.PasswordMismatch -> R.string.auth_error_confirm_password_mismatch
            is AuthException.InvalidCredentials -> R.string.auth_error_invalid_credentials
            is AuthException.UserNotFound -> R.string.auth_error_user_not_found
            is AuthException.UserAlreadyExists -> R.string.auth_error_user_already_exists
            is UnknownHostException -> R.string.auth_error_network
            else -> R.string.auth_error_unknown
        }
    }

    public fun updateDisplayName(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(displayName = currentState.displayName.validate(value), errorResId = null)
        }
    }

    public fun updateEmail(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(email = currentState.email.validate(value), errorResId = null)
        }
        if (currentState is AuthUiState.SignIn) {
            _uiState.value = currentState.copy(email = currentState.email.validate(value), errorResId = null)
        }
        if (currentState is AuthUiState.ForgotPassword) {
            _uiState.value = currentState.copy(email = currentState.email.validate(value), errorResId = null)
        }
    }

    public fun updatePassword(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            val updatedPassword = currentState.password.validate(value)
            val updatedConfirmPassword = currentState.confirmPassword.validate(currentState.confirmPassword.value, value)
            _uiState.value = currentState.copy(
                password = updatedPassword,
                confirmPassword = updatedConfirmPassword,
                errorResId = null
            )
        }
        if (currentState is AuthUiState.SignIn) {
            _uiState.value = currentState.copy(password = currentState.password.validate(value), errorResId = null)
        }
    }

    public fun updateConfirmPassword(value: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.SignUp) {
            _uiState.value = currentState.copy(
                confirmPassword = currentState.confirmPassword.validate(value, currentState.password.value),
                errorResId = null
            )
        }
    }
}