package dev.shoheiyamagiwa.constell.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.ui.theme.Blue600
import dev.shoheiyamagiwa.constell.ui.theme.Purple600

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
public fun AppLogo(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(48.dp).clip(shape = RoundedCornerShape(size = 12.dp)).background(Brush.linearGradient(colors = listOf(Blue600, Purple600))).padding(all = 8.dp)) {
        Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
    }
}