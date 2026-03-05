package dev.shoheiyamagiwa.constell.feature.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.composable.AppLogo
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.feature.auth.composable.AuthHeader
import dev.shoheiyamagiwa.constell.feature.auth.composable.TabSwitcher
import dev.shoheiyamagiwa.constell.ui.theme.Purple500

@Composable
public fun AuthScreen(viewModel: AuthViewModel = viewModel(factory = authViewModelFactory)) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.validateSession()
    }

    Background(alignment = Alignment.Center) {
        when (screenState) {
            is AuthScreenState.Loading -> {
                CircularProgressIndicator()
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize().systemBarsPadding().navigationBarsPadding().padding(all = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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

                    TabSwitcher()

                    TODO("We should place AuthForm composable here")
                }
            }
        }
    }
}