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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.feature.auth.AuthScreenState
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate400
import dev.shoheiyamagiwa.constell.ui.theme.Slate800

// TODO: This composable should be refactored to make it on the "compose" way
@Composable
public fun TabSwitcher(authScreenState: AuthScreenState, modifier: Modifier = Modifier, onChangeToSignIn: () -> Unit, onChangeToSignUp: () -> Unit) {
    val isSignIn = authScreenState is AuthScreenState.SignIn
    val isSignUp = authScreenState is AuthScreenState.SignUp

    fun textColor(condition: Boolean) = if (condition) Color.White else Slate400.copy(alpha = 0.5F)

    val currentTabIndex = when (authScreenState) {
        is AuthScreenState.Loading -> 0 // Fail-safe
        is AuthScreenState.SignIn -> 0
        is AuthScreenState.SignUp -> 1
    }
    var composableSize by remember { mutableStateOf(value = IntSize.Zero) }
    val margin = 16.dp.value
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
        drawRoundRect(color = Slate100, alpha = 0.05F, topLeft = animatedTabOffset, size = tabSize, cornerRadius = CornerRadius(x = tabRadius, y = tabRadius))
    }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TabSwitch(text = "Sign In", color = textColor(condition = isSignIn), onClick = onChangeToSignIn, modifier = Modifier.weight(0.5F))
            TabSwitch(text = "Sign Up", color = textColor(condition = isSignUp), onClick = onChangeToSignUp, modifier = Modifier.weight(0.5F))
        }
    }
}

@Preview
@Composable
private fun TabSwitcherPreview_SignIn() {
    TabSwitcher(authScreenState = AuthScreenState.SignIn(), onChangeToSignIn = {}, onChangeToSignUp = {})
}

@Preview
@Composable
private fun TabSwitcherPreview_SignUp() {
    TabSwitcher(authScreenState = AuthScreenState.SignUp(), onChangeToSignIn = {}, onChangeToSignUp = {})
}

@Composable
private fun TabSwitch(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(contentAlignment = Alignment.Center, modifier = modifier.clip(shape = RoundedCornerShape(percent = 50)).clickable(interactionSource = interactionSource, indication = null, onClick = onClick).padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(text = text, color = color, textAlign = TextAlign.Center)
    }
}