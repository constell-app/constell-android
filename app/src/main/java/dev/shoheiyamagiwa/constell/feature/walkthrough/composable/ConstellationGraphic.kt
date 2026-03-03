package dev.shoheiyamagiwa.constell.feature.walkthrough.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import dev.shoheiyamagiwa.constell.feature.walkthrough.walkthroughData
import dev.shoheiyamagiwa.constell.ui.theme.Blue500
import dev.shoheiyamagiwa.constell.ui.theme.Purple500

data class NodePoint(
    val id: Int,
    val x: Float,
    val y: Float,
    val radius: Float,
    val isMain: Boolean = false
)

val nodes = listOf(
    NodePoint(0, 150.0F, 150.0F, 8.0F, true),
    NodePoint(1, 150.0F, 40.0F, 5.0F),
    NodePoint(2, 255.0F, 110.0F, 4.0F),
    NodePoint(3, 215.0F, 230.0F, 6.0F),
    NodePoint(4, 85.0F, 230.0F, 5.0F),
    NodePoint(5, 45.0F, 110.0F, 4.0F)
)

data class EdgeLine(val id: Int, val startX: Float, val startY: Float, val endX: Float, val endY: Float)

val edges = listOf(
    EdgeLine(1, 150.0F, 150.0F, 150.0F, 40.0F),
    EdgeLine(2, 150.0F, 150.0F, 255.0F, 110.0F),
    EdgeLine(3, 150.0F, 150.0F, 215.0F, 230.0F),
    EdgeLine(4, 150.0F, 150.0F, 85.0F, 230.0F),
    EdgeLine(5, 150.0F, 150.0F, 45.0F, 110.0F),
    EdgeLine(6, 150.0F, 40.0F, 255.0F, 110.0F),
    EdgeLine(7, 215.0F, 230.0F, 85.0F, 230.0F),
    EdgeLine(8, 45.0F, 110.0F, 150.0F, 40.0F)
)

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
public fun ConstellationGraphic(
    @PreviewParameter(provider = WalkthroughStepProvider::class) walkthroughStep: WalkthroughStep,
    modifier: Modifier = Modifier
) {
    val satelliteScale by animateFloatAsState(
        targetValue = if (walkthroughStep.showSatelliteNodes) 1.0F else 0.0F,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "satelliteScale"
    )

    val lineProgress by animateFloatAsState(
        targetValue = if (walkthroughStep.showEdges) 1.0F else 0.0F,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "lineProgress"
    )

    val mainColor by animateColorAsState(
        targetValue = if (walkthroughStep.showLogo) Purple500 else Blue500,
        animationSpec = tween(durationMillis = 800),
        label = "mainColor"
    )

    val lineBrush = Brush.linearGradient(colors = listOf(Blue500, Purple500))

    val radialGradientColor by animateColorAsState(targetValue = if (walkthroughStep.showLogo) Purple500 else Blue500, animationSpec = tween(durationMillis = 1000))
    val radialGradientAlpha by animateFloatAsState(targetValue = if (walkthroughStep.showEdges) 0.2F else 0.0F, animationSpec = tween(durationMillis = 1000))

    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth().drawBehind {
        val brush = Brush.radialGradient(colors = listOf(radialGradientColor.copy(alpha = radialGradientAlpha), Color.Transparent))

        drawRect(brush = brush)
    }) {
        Canvas(modifier = Modifier.aspectRatio(1.0F)) {
            val scaleFactor = size.width / 300.0F

            scale(scaleFactor, pivot = Offset.Zero) {
                if (lineProgress > 0.0F) {
                    edges.forEach { line ->
                        val currentX2 = line.startX + (line.endX - line.startX) * lineProgress
                        val currentY2 = line.startY + (line.endY - line.startY) * lineProgress

                        drawLine(
                            brush = lineBrush,
                            start = Offset(x = line.startX, y = line.startY),
                            end = Offset(x = currentX2, y = currentY2),
                            strokeWidth = 1.5F,
                            alpha = 0.6F,
                            cap = StrokeCap.Round
                        )
                    }
                }

                nodes.forEach { node ->
                    if (node.isMain) {
                        if (walkthroughStep.showMainNode) {
                            drawCircle(
                                brush = Brush.radialGradient(colors = listOf(mainColor.copy(alpha = 0.05F), Color.Transparent), center = Offset(x = node.x, y = node.y), radius = node.radius * 6.0F),
                                center = Offset(x = node.x, y = node.y),
                                radius = node.radius * 6.0F
                            )
                            drawCircle(
                                color = mainColor,
                                center = Offset(x = node.x, y = node.y),
                                radius = node.radius
                            )
                        }
                    } else {
                        if (satelliteScale > 0.0F) {
                            drawCircle(
                                color = Blue500,
                                center = Offset(x = node.x, y = node.y),
                                radius = node.radius * satelliteScale
                            )
                        }
                    }
                }
            }
        }
    }
}

private class WalkthroughStepProvider : PreviewParameterProvider<WalkthroughStep> {
    override val values = sequenceOf(walkthroughData[0], walkthroughData[1], walkthroughData[2], walkthroughData[3])
}