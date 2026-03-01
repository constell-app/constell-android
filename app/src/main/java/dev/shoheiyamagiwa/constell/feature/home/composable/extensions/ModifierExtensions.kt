package dev.shoheiyamagiwa.constell.feature.home.composable.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draw a glow effect on the component.
 *
 * @author shohei-yamagiwa
 */
fun Modifier.glow(
    color: Color,
    radius: Dp,
    alpha: Float = 0.3F
) = this.drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = size.minDimension / 2 + radius.toPx()
        )
    )
}

/**
 * Draw dots on the background of the component.
 *
 * @author shohei-yamagiwa
 */
fun Modifier.dotBackground(
    dotColor: Color = Color(0xFF475569),
    dotRadius: Dp = 1.dp,
    spacing: Dp = 30.dp,
    alpha: Float = 0.2F
) = this.drawBehind {
    val radiusPx = dotRadius.toPx()
    val spacingPx = spacing.toPx()

    val columns = (size.width / spacingPx).toInt() + 1
    val rows = (size.height / spacingPx).toInt() + 1

    for (xIndex in 0..columns) {
        for (yIndex in 0..rows) {
            val offset = Offset(
                x = xIndex * spacingPx + radiusPx + (spacingPx / 4),
                y = yIndex * spacingPx + radiusPx + (spacingPx / 4)
            )

            drawCircle(color = dotColor, radius = radiusPx, center = offset, alpha = alpha)
        }
    }
}