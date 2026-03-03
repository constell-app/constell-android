package dev.shoheiyamagiwa.constell.feature.walkthrough.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.ui.theme.Blue500
import dev.shoheiyamagiwa.constell.ui.theme.Slate700

public data class IndicatorState(val currentPage: Int, val totalPages: Int)

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
public fun WalkthroughIndicator(@PreviewParameter(provider = IndicatorStatePreviewProvider::class) state: IndicatorState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(times = state.totalPages) { index ->
            val isSelected = state.currentPage == index
            val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 8.dp, label = "indicator")
            val color by animateColorAsState(targetValue = if (isSelected) Blue500 else Slate700, label = "indicatorColor")

            Box(modifier = Modifier.height(8.dp).width(width).clip(CircleShape).background(color))
        }
    }
}

private class IndicatorStatePreviewProvider : PreviewParameterProvider<IndicatorState> {
    override val values: Sequence<IndicatorState> = sequenceOf(IndicatorState(currentPage = 0, totalPages = 4), IndicatorState(currentPage = 1, totalPages = 4), IndicatorState(currentPage = 2, totalPages = 4), IndicatorState(currentPage = 3, totalPages = 4))
}