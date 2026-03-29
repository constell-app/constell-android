package dev.shoheiyamagiwa.constell.feature.home.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.feature.home.composable.extensions.glow
import dev.shoheiyamagiwa.constell.ui.theme.Blue400
import dev.shoheiyamagiwa.constell.ui.theme.Blue500
import dev.shoheiyamagiwa.constell.ui.theme.Purple500
import dev.shoheiyamagiwa.constell.ui.theme.Slate300
import dev.shoheiyamagiwa.constell.ui.theme.Slate600
import dev.shoheiyamagiwa.constell.ui.theme.Slate800

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
fun CenterOrbitNode(
    @PreviewParameter(provider = OrbitTitleProvider::class) title: String,
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    growRadius: Dp = 16.dp,
    isFocusing: Boolean = false,
    onClick: () -> Unit = {}
) {
    val baseSize = 128.dp
    val fontSize = (16.0F * (size.value / baseSize.value))
    val fontHeight = (16.0F * (size.value / baseSize.value) * 1.5F)

    val targetBorderColor = if (isFocusing) Purple500.copy(alpha = 0.8F) else Slate600
    val targetGlowColor = if (isFocusing) Purple500 else Blue500

    val borderColor by animateColorAsState(targetValue = targetBorderColor, label = "borderColor")
    val glowColor by animateColorAsState(targetValue = targetGlowColor, label = "glowColor")

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1F,
        targetValue = 0.3F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .glow(
                color = glowColor,
                radius = if (isFocusing) growRadius else growRadius * 0.8F,
                alpha = 0.4F
            )
            .clip(CircleShape)
            .background(color = Slate800.copy(alpha = 0.8F))
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Blue500.copy(alpha = pulseAlpha),
                            Purple500.copy(alpha = pulseAlpha)
                        )
                    )
                )
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = fontHeight.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(size * 0.1F)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SatelliteOrbitNode(
    @PreviewParameter(provider = OrbitTitleProvider::class) title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    size: Dp = 72.dp
) {
    val baseSize = 72.dp
    val fontSize = (10.0F * (size.value / baseSize.value))
    val fontHeight = (10.0F * (size.value / baseSize.value) * 1.5F)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "satelliteScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Blue400 else Slate600,
        label = "satelliteBorderColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else Slate300,
        label = "satelliteTextColor"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(Slate800)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(size * 0.1F)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = fontHeight.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private class OrbitTitleProvider : PreviewParameterProvider<String> {
    override val values =
        sequenceOf("Next.js", "Spring Boot", "Jetpack Compose", "Tauri", "Supabase")
}