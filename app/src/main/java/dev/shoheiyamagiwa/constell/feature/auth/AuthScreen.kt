package dev.shoheiyamagiwa.constell.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.composable.CustomTextField
import dev.shoheiyamagiwa.constell.composable.ErrorAlert
import dev.shoheiyamagiwa.constell.feature.auth.composable.AuthHeader
import dev.shoheiyamagiwa.constell.feature.auth.composable.AuthLogoWithGradient
import dev.shoheiyamagiwa.constell.feature.auth.composable.AuthSubmitButton
import dev.shoheiyamagiwa.constell.feature.auth.composable.TabSwitcher
import dev.shoheiyamagiwa.constell.ui.theme.Blue400
import org.koin.compose.viewmodel.koinViewModel

@Composable
public fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToConfirmEmail: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val successMessage = stringResource(id = R.string.auth_success_password_reset_email_sent)

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.NavigateToConfirmEmail -> {
                    onNavigateToConfirmEmail(event.email)
                }

                is AuthUiEvent.NavigateToHome -> {
                    onNavigateToHome()
                }

                is AuthUiEvent.PasswordResetEmailSent -> {
                    // TODO: Navigate to password reset email sent screen
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.validateSession()
    }

    AuthContent(
        uiState = uiState,
        onUpdateEmail = { viewModel.updateEmail(value = it) },
        onUpdatePassword = { viewModel.updatePassword(value = it) },
        onUpdateConfirmPassword = { viewModel.updateConfirmPassword(value = it) },
        onUpdateDisplayName = { viewModel.updateDisplayName(value = it) },
        onTabChangeToSignUp = { viewModel.changeToSignUpUi() },
        onTabChangeToSignIn = { viewModel.changeToSignInUi() },
        onChangeToForgotPassword = { viewModel.changeToForgotPasswordUi() },
        onSubmit = { viewModel.submit() }
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onUpdateConfirmPassword: (String) -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onTabChangeToSignUp: () -> Unit,
    onTabChangeToSignIn: () -> Unit,
    onChangeToForgotPassword: () -> Unit,
    onSubmit: () -> Unit
) {
    // TODO: Getting UI texts from string resources
    Background(alignment = Alignment.Center) {
        when (uiState) {
            AuthUiState.Loading -> {
                CircularProgressIndicator()
            }

            is AuthUiState.SignIn -> {
                SignInContent(
                    uiState = uiState,
                    onUpdateEmail = onUpdateEmail,
                    onUpdatePassword = onUpdatePassword,
                    onTabChangeToSignUp = onTabChangeToSignUp,
                    onChangeToForgotPassword = onChangeToForgotPassword,
                    onSubmit = onSubmit
                )
            }

            is AuthUiState.SignUp -> {
                SignUpContent(
                    uiState = uiState,
                    onUpdateDisplayName = onUpdateDisplayName,
                    onUpdateEmail = onUpdateEmail,
                    onUpdatePassword = onUpdatePassword,
                    onUpdateConfirmPassword = onUpdateConfirmPassword,
                    onTabChangeToSignIn = onTabChangeToSignIn,
                    onSubmit = onSubmit
                )
            }

            is AuthUiState.ForgotPassword -> {
                ForgotPasswordContent(
                    uiState = uiState,
                    onUpdateEmail = onUpdateEmail,
                    onTabChangeToSignIn = onTabChangeToSignIn,
                    onSubmit = onSubmit
                )
            }
        }
    }
}

@Composable
private fun SignUpContent(
    uiState: AuthUiState.SignUp,
    onUpdateDisplayName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onUpdateConfirmPassword: (String) -> Unit,
    onTabChangeToSignIn: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        AuthHeader(
            modifier = Modifier.fillMaxWidth(),
            logo = { AuthLogoWithGradient() },
            title = stringResource(id = R.string.auth_text_signup_title),
            subtitle = stringResource(id = R.string.auth_text_signup_subtitle)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TabSwitcher(
            modifier = Modifier.fillMaxWidth(),
            authUiState = uiState,
            onChangeToSignIn = onTabChangeToSignIn,
            onChangeToSignUp = {}
        )

        AnimatedVisibility(
            visible = uiState.errorResId != null,
            enter = expandVertically(animationSpec = spring()),
            exit = shrinkVertically(animationSpec = spring())
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorAlert(
                    message = uiState.errorResId?.let { stringResource(id = it) } ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            // Display Name
            CustomTextField(
                value = uiState.displayName.value,
                onValueChange = onUpdateDisplayName,
                placeholder = stringResource(id = R.string.auth_label_display_name),
                leadingIcon = Icons.Default.Person,
                isError = uiState.displayName.hasError()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            CustomTextField(
                value = uiState.email.value,
                onValueChange = onUpdateEmail,
                placeholder = stringResource(id = R.string.auth_label_email),
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.email.hasError()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            var isValueMasked by remember { mutableStateOf(value = true) }
            CustomTextField(
                value = uiState.password.value,
                onValueChange = onUpdatePassword,
                placeholder = stringResource(id = R.string.auth_label_password),
                leadingIcon = Icons.Default.Lock,
                maskValue = true,
                isValueMasked = isValueMasked,
                onMaskToggled = { isValueMasked = !isValueMasked },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = uiState.password.hasError()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            var isValueMasked2 by remember { mutableStateOf(value = true) }
            CustomTextField(
                value = uiState.confirmPassword.value,
                onValueChange = onUpdateConfirmPassword,
                placeholder = stringResource(id = R.string.auth_label_confirm_password),
                leadingIcon = Icons.Default.Lock,
                maskValue = true,
                isValueMasked = isValueMasked2,
                onMaskToggled = { isValueMasked2 = !isValueMasked2 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = uiState.confirmPassword.hasError()
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthSubmitButton(
                text = stringResource(id = R.string.auth_button_create_account),
                onClick = onSubmit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SignInContent(
    uiState: AuthUiState.SignIn,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onTabChangeToSignUp: () -> Unit,
    onChangeToForgotPassword: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        AuthHeader(
            modifier = Modifier.fillMaxWidth(),
            logo = { AuthLogoWithGradient() },
            title = stringResource(id = R.string.auth_text_signin_title),
            subtitle = stringResource(id = R.string.auth_text_signin_subtitle)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TabSwitcher(
            modifier = Modifier.fillMaxWidth(),
            authUiState = uiState,
            onChangeToSignIn = {},
            onChangeToSignUp = onTabChangeToSignUp
        )

        AnimatedVisibility(
            visible = uiState.errorResId != null,
            enter = expandVertically(animationSpec = spring()),
            exit = shrinkVertically(animationSpec = spring())
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorAlert(
                    message = uiState.errorResId?.let { stringResource(id = it) } ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            // Email
            CustomTextField(
                value = uiState.email.value,
                onValueChange = onUpdateEmail,
                placeholder = stringResource(id = R.string.auth_label_email),
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.email.hasError()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            var isValueMasked by remember { mutableStateOf(value = true) }
            CustomTextField(
                value = uiState.password.value,
                onValueChange = onUpdatePassword,
                placeholder = stringResource(id = R.string.auth_label_password),
                leadingIcon = Icons.Default.Lock,
                maskValue = true,
                isValueMasked = isValueMasked,
                onMaskToggled = { isValueMasked = !isValueMasked },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = uiState.password.hasError()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(contentPadding = PaddingValues(all = 0.dp), onClick = onChangeToForgotPassword) {
                    Text(
                        stringResource(id = R.string.auth_button_forgot_password),
                        color = Blue400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthSubmitButton(
                text = stringResource(id = R.string.auth_button_sign_in),
                onClick = onSubmit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ForgotPasswordContent(
    uiState: AuthUiState.ForgotPassword,
    onUpdateEmail: (String) -> Unit,
    onTabChangeToSignIn: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        AuthHeader(
            modifier = Modifier.fillMaxWidth(),
            logo = { AuthLogoWithGradient() },
            title = stringResource(id = R.string.auth_text_forgot_password_title),
            subtitle = stringResource(id = R.string.auth_text_forgot_password_subtitle)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // No TabSwitcher here as it's a sub-flow of SignIn

        AnimatedVisibility(
            visible = uiState.errorResId != null,
            enter = expandVertically(animationSpec = spring()),
            exit = shrinkVertically(animationSpec = spring())
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorAlert(
                    message = uiState.errorResId?.let { stringResource(id = it) } ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            // Email
            CustomTextField(
                value = uiState.email.value,
                onValueChange = onUpdateEmail,
                placeholder = stringResource(id = R.string.auth_label_email),
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.email.hasError()
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthSubmitButton(
                text = stringResource(id = R.string.auth_button_send_reset_link),
                onClick = onSubmit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onTabChangeToSignIn) {
                    Text(
                        stringResource(id = R.string.auth_button_back_to_signin),
                        color = Blue400,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun AuthContentPreview_Loading() {
    AuthContent(
        uiState = AuthUiState.Loading,
        onUpdateEmail = {},
        onUpdatePassword = {},
        onUpdateConfirmPassword = {},
        onUpdateDisplayName = {},
        onTabChangeToSignUp = {},
        onTabChangeToSignIn = {},
        onChangeToForgotPassword = {},
        onSubmit = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthContentPreview_SignIn() {
    Background(alignment = Alignment.Center) {
        SignInContent(
            uiState = AuthUiState.SignIn(
                email = FormField.EmailField(value = "constell@example.com"),
                password = FormField.PasswordField(value = "LoremIpsum123")
            ),
            onUpdateEmail = {},
            onUpdatePassword = {},
            onTabChangeToSignUp = {},
            onChangeToForgotPassword = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthContentPreview_SignUp() {
    Background(alignment = Alignment.Center) {
        SignUpContent(
            uiState = AuthUiState.SignUp(
                displayName = FormField.DisplayNameField(value = "Constell User"),
                email = FormField.EmailField(value = "constell@example.com"),
                password = FormField.PasswordField(value = "LoremIpsum123"),
                confirmPassword = FormField.ConfirmPasswordField(value = "LoremIpsum123")
            ),
            onUpdateDisplayName = {},
            onUpdateEmail = {},
            onUpdatePassword = {},
            onUpdateConfirmPassword = {},
            onTabChangeToSignIn = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthContentPreview_ForgotPassword() {
    Background(alignment = Alignment.Center) {
        ForgotPasswordContent(
            uiState = AuthUiState.ForgotPassword(
                email = FormField.EmailField(value = "constell@example.com")
            ),
            onUpdateEmail = {},
            onTabChangeToSignIn = {},
            onSubmit = {}
        )
    }
}