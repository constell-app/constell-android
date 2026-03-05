package dev.shoheiyamagiwa.constell.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shoheiyamagiwa.constell.ui.theme.Slate950

@Composable
public fun Background(modifier: Modifier = Modifier, alignment: Alignment = Alignment.TopStart, content: @Composable () -> Unit) {
    Box(contentAlignment = alignment, modifier = modifier.fillMaxSize().background(color = Slate950)) {
        content()
    }
}