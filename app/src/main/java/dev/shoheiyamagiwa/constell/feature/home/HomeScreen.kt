package dev.shoheiyamagiwa.constell.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.feature.home.composable.ConstellationWorld
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme

@Composable
public fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        var isFocusing by remember { mutableStateOf(false) }
        ConstellationWorld(
            modifier = Modifier,
            nodeGap = 144.dp,
            edgeWidth = 2.dp,
            centerNodeTitle = "Next.js",
            onCenterNodeClick = {
                isFocusing = !isFocusing
            },
            satelliteNodeTitles = listOf(
                "React",
                "Vercel",
                "Server Component",
                "v0.dev",
                "Tailwind CSS",
                "Image Optimization",
            ),
            onSatelliteNodeClick = { nodeId ->

            },
            isFocusing = isFocusing
        )
    }
}