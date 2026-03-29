package dev.shoheiyamagiwa.constell.feature.auth.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.composable.AppLogo
import dev.shoheiyamagiwa.constell.ui.theme.Purple500

@Composable
public fun AuthLogoWithGradient(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Gradient for an entire background
                val radius = size.maxDimension / 1.5F
                val brush = Brush.radialGradient(
                    colors = listOf(Purple500.copy(alpha = 0.1F), Color.Transparent),
                    center = center,
                    radius = radius
                )

                drawCircle(brush, radius)
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.drawBehind {
                // Gradient for the logo
                val radius = (size.maxDimension / 2.0F) + 16.dp.toPx()
                val brush = Brush.radialGradient(
                    colors = listOf(Purple500.copy(alpha = 0.3F), Color.Transparent),
                    center = center,
                    radius = radius
                )

                drawCircle(brush, radius)
            }
        ) {
            AppLogo()
        }
    }
}
