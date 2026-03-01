package dev.shoheiyamagiwa.constell.feature.home.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.feature.home.composable.extensions.dotBackground
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConstellationWorld(
    modifier: Modifier = Modifier,
    centerNodeTitle: String,
    centerNodeRadius: Dp = 64.dp,
    satelliteNodeTitles: List<String>,
    satelliteNodeRadius: Dp = 44.dp,
    nodeGap: Dp = 150.dp,
    edgeWidth: Dp,
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F172A))
            .dotBackground(
                dotColor = Color(0xFF475569),
                dotRadius = 1.5.dp,
                spacing = 30.dp,
                alpha = 0.3F
            )
    ) {
        Layout(
            content = {
                Box(modifier = Modifier.layoutId("centerNode")) {
                    CenterOrbitNode(title = centerNodeTitle, size = centerNodeRadius * 2)
                }
                satelliteNodeTitles.forEach {
                    Box(modifier = Modifier.layoutId("satelliteNode")) {
                        SatelliteOrbitNode(title = it, size = satelliteNodeRadius * 2)
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (satelliteNodeTitles.isEmpty()) {
                        return@drawBehind
                    }
                    val layoutWidth = size.width
                    val layoutHeight = size.height

                    val centerX = layoutWidth / 2
                    val centerY = layoutHeight / 2

                    val angleUnit = 360.0F / satelliteNodeTitles.size
                    val distance = nodeGap.toPx()

                    for (index in 0 until satelliteNodeTitles.size) {
                        val angle = angleUnit * index
                        val angleRadian = (angle / 180) * PI.toFloat()

                        drawLine(
                            color = Color(0xFFFFFFFF),
                            start = Offset(
                                x = centerX + (centerNodeRadius.toPx() * cos(angleRadian)),
                                y = centerY + (centerNodeRadius.toPx() * sin(angleRadian))
                            ),
                            end = Offset(
                                x = centerX + ((distance - satelliteNodeRadius.toPx()) * cos(
                                    angleRadian
                                )),
                                y = centerY + ((distance - satelliteNodeRadius.toPx()) * sin(
                                    angleRadian
                                ))
                            ),
                            strokeWidth = edgeWidth.toPx()
                        )
                    }
                }
        ) { measurables, constraints ->
            val centerMeasurables = measurables.filter { it.layoutId == "centerNode" }
            val satelliteMeasurables = measurables.filter { it.layoutId == "satelliteNode" }

            val childrenConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            val placeableCenterContents = centerMeasurables.map { measurable ->
                measurable.measure(childrenConstraints)
            }

            val placeableSatelliteContents = satelliteMeasurables.map { measurable ->
                measurable.measure(childrenConstraints)
            }

            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight

            layout(layoutWidth, layoutHeight) {
                val centerX = layoutWidth / 2
                val centerY = layoutHeight / 2

                // Place the content in the center of the parent composable
                placeableCenterContents.forEach { content ->
                    content.placeRelative(
                        x = centerX - (content.width / 2),
                        y = centerY - (content.height / 2)
                    )
                }

                // Place the satellite contents around the center contents
                if (placeableSatelliteContents.isNotEmpty()) {
                    val angleUnit = 360.0F / placeableSatelliteContents.size
                    val distance = nodeGap.toPx()

                    placeableSatelliteContents.forEachIndexed { index, content ->
                        val contentPosX = centerX - (content.width / 2)
                        val contentPosY = centerY - (content.height / 2)
                        val angle = angleUnit * index
                        val angleRadian = (angle / 180) * PI

                        content.placeRelative(
                            x = contentPosX + (distance * cos(angleRadian)).toInt(),
                            y = contentPosY + (distance * sin(angleRadian)).toInt()
                        )
                    }
                }
            }
        }
    }
}