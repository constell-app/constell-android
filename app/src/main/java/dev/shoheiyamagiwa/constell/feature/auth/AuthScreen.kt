package dev.shoheiyamagiwa.constell.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.composable.AppLogo
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.composable.CustomTextField
import dev.shoheiyamagiwa.constell.feature.auth.composable.AuthHeader
import dev.shoheiyamagiwa.constell.feature.auth.composable.TabSwitcher
import dev.shoheiyamagiwa.constell.ui.theme.Blue400
import dev.shoheiyamagiwa.constell.ui.theme.Blue600
import dev.shoheiyamagiwa.constell.ui.theme.Purple500
import dev.shoheiyamagiwa.constell.ui.theme.Purple600

import org.koin.compose.viewmodel.koinViewModel

@Composable
public fun AuthScreen(viewModel: AuthViewModel = koinViewModel()) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val currentDisplayName = when (val state = screenState) {
        is AuthScreenState.SignUp -> state.displayName
        else -> ""
    }

    val currentEmail = when (val state = screenState) {
        is AuthScreenState.SignUp -> state.email
        is AuthScreenState.SignIn -> state.email
        else -> ""
    }

    val currentPassword = when (val state = screenState) {
        is AuthScreenState.SignUp -> state.password
        is AuthScreenState.SignIn -> state.password
        else -> ""
    }

    val currentConfirmPassword = when (val state = screenState) {
        is AuthScreenState.SignUp -> state.confirmPassword
        else -> ""
    }

    fun onDisplayNameChanged(value: String) {
        viewModel.updateDisplayName(value)
    }

    fun onEmailChanged(value: String) {
        viewModel.updateEmail(value)
    }

    fun onPasswordChanged(value: String) {
        viewModel.updatePassword(value)
    }

    fun onConfirmPasswordChanged(value: String) {
        viewModel.updateConfirmPassword(value)
    }

    fun onSubmit() {
        viewModel.submit()
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.validateSession()
    }

    // TODO: Getting UI texts from string resources
    Background(alignment = Alignment.Center) {
        when (screenState) {
            AuthScreenState.Loading -> {
                CircularProgressIndicator()
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.height(48.dp))

                    AuthHeader(
                        modifier = Modifier.fillMaxWidth(),
                        logo = {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().drawBehind {
                                // Gradient for an entire background
                                val radius = size.maxDimension / 1.5F
                                val brush = Brush.radialGradient(colors = listOf(Purple500.copy(alpha = 0.1F), Color.Transparent), center = center, radius = radius)

                                drawCircle(brush, radius)
                            }) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.drawBehind {
                                    // Gradient for the logo
                                    val radius = (size.maxDimension / 2.0F) + 16.dp.toPx()
                                    val brush = Brush.radialGradient(colors = listOf(Purple500.copy(alpha = 0.3F), Color.Transparent), center = center, radius = radius)

                                    drawCircle(brush, radius)
                                }) {
                                    AppLogo()
                                }
                            }
                        },
                        title = stringResource(id = R.string.auth_text_signup_title),
                        subtitle = stringResource(id = R.string.auth_text_signup_subtitle)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    TabSwitcher(
                        modifier = Modifier.fillMaxWidth(), authScreenState = screenState,
                        onChangeToSignIn = {
                            viewModel.updateToSignIn()
                        }, onChangeToSignUp = {
                            viewModel.updateToSignUp()
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Display Name
                        AnimatedVisibility(visible = screenState is AuthScreenState.SignUp, enter = expandVertically(animationSpec = spring()), exit = shrinkVertically(animationSpec = spring())) {
                            CustomTextField(value = currentDisplayName, onValueChange = { onDisplayNameChanged(value = it) }, placeholder = "Display Name", leadingIcon = Icons.Default.Person)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        CustomTextField(value = currentEmail, onValueChange = { onEmailChanged(value = it) }, placeholder = "Email", leadingIcon = Icons.Default.Email, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password
                        var isValueMasked by remember { mutableStateOf(value = true) }
                        CustomTextField(
                            value = currentPassword,
                            onValueChange = { onPasswordChanged(value = it) },
                            placeholder = "Password",
                            leadingIcon = Icons.Default.Lock,
                            maskValue = true,
                            isValueMasked = isValueMasked,
                            onMaskToggled = { isValueMasked = !isValueMasked },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password
                        AnimatedVisibility(visible = screenState is AuthScreenState.SignUp, enter = expandVertically(animationSpec = spring()), exit = shrinkVertically(animationSpec = spring())) {
                            var isValueMasked by remember { mutableStateOf(value = true) }
                            CustomTextField(
                                value = currentConfirmPassword,
                                onValueChange = { onConfirmPasswordChanged(value = it) },
                                placeholder = "Confirm Password",
                                leadingIcon = Icons.Default.Lock,
                                maskValue = true,
                                isValueMasked = isValueMasked,
                                onMaskToggled = { isValueMasked = !isValueMasked },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // FIXME: I have to remove the padding of this block of composables
                        AnimatedVisibility(visible = screenState is AuthScreenState.SignIn) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                TextButton(contentPadding = PaddingValues(all = 0.dp), onClick = { /* TODO */ }) {
                                    Text("Forgot Password?", color = Blue400, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(onClick = { onSubmit() }, shape = RoundedCornerShape(size = 12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), contentPadding = PaddingValues(), modifier = Modifier.fillMaxWidth().height(56.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = listOf(Blue600, Purple600))), contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val text = if (screenState is AuthScreenState.SignIn) "Sign In" else "Create Account"

                                    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}