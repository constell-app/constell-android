package dev.shoheiyamagiwa.constell.feature.auth.composable

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.auth.AuthUiState
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate400
import dev.shoheiyamagiwa.constell.ui.theme.Slate800

// TODO: Refactor this composable to be more idiomatic Compose
@Composable
public fun TabSwitcher(authUiState: AuthUiState, modifier: Modifier = Modifier, onChangeToSignIn: () -> Unit, onChangeToSignUp: () -> Unit) {
    val isSignIn = authUiState is AuthUiState.SignIn
    val isSignUp = authUiState is AuthUiState.SignUp

    fun textColor(condition: Boolean) = if (condition) Color.White else Slate400.copy(alpha = 0.5F)

    val currentTabIndex = when (authUiState) {
        is AuthUiState.Loading -> 0 // Fail-safe
        is AuthUiState.SignIn -> 0
        is AuthUiState.SignUp -> 1
    }
    var composableSize by remember { mutableStateOf(value = IntSize.Zero) }
    val density = LocalDensity.current
    val margin = with(receiver = density) { 4.dp.toPx() }
    val tabWidth = (composableSize.width - margin * 4.0F) / 2.0F
    val tabHeight = composableSize.height - margin * 2.0F
    val tabRadius = tabHeight / 2.0F
    val tabSize = Size(width = tabWidth, height = tabHeight)
    val tabOffset = Offset(x = if (currentTabIndex == 0) margin else margin + tabWidth + margin * 2.0F, y = margin)
    val animatedTabOffset by animateOffsetAsState(targetValue = tabOffset, animationSpec = spring())

    Box(modifier = modifier.clip(shape = RoundedCornerShape(percent = 50)).background(color = Slate800.copy(alpha = 0.5F)).onSizeChanged { size ->
        composableSize = size
    }.drawWithContent {
        drawContent()

        if (tabWidth > 0.0F && tabHeight > 0.0F) {
            drawRoundRect(color = Slate100, alpha = 0.05F, topLeft = animatedTabOffset, size = tabSize, cornerRadius = CornerRadius(x = tabRadius, y = tabRadius))
        }
    }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TabSwitch(text = stringResource(id = R.string.auth_button_sign_in), color = textColor(condition = isSignIn), onClick = onChangeToSignIn, modifier = Modifier.weight(0.5F))
            TabSwitch(text = stringResource(id = R.string.auth_button_sign_up), color = textColor(condition = isSignUp), onClick = onChangeToSignUp, modifier = Modifier.weight(0.5F))
        }
    }
}

@Preview
@Composable
private fun TabSwitcherPreview_SignIn() {
    TabSwitcher(authUiState = AuthUiState.SignIn(), onChangeToSignIn = {}, onChangeToSignUp = {})
}

@Preview
@Composable
private fun TabSwitcherPreview_SignUp() {
    TabSwitcher(authUiState = AuthUiState.SignUp(), onChangeToSignIn = {}, onChangeToSignUp = {})
}

@Composable
private fun TabSwitch(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(contentAlignment = Alignment.Center, modifier = modifier.clip(shape = RoundedCornerShape(percent = 50)).clickable(interactionSource = interactionSource, indication = null, onClick = onClick).padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(text = text, color = color, textAlign = TextAlign.Center)
    }
}