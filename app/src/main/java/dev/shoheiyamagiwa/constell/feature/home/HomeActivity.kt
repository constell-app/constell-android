package dev.shoheiyamagiwa.constell.feature.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shoheiyamagiwa.constell.feature.home.composable.CenterOrbitNode
import dev.shoheiyamagiwa.constell.feature.home.composable.ConstellationWorld
import dev.shoheiyamagiwa.constell.feature.home.composable.SatelliteOrbitNode
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            )
        )
        setContent {
            ConstellTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        ConstellationWorld(
                            modifier = Modifier,
                            nodeGap = 144.dp,
                            edgeWidth = 1.dp,
                            centerContent = {
                                CenterOrbitNode(title = "Next.js")
                            },
                            satelliteContents = listOf(
                                { SatelliteOrbitNode(title = "React", size = 88.dp) },
                                { SatelliteOrbitNode(title = "Vercel", size = 88.dp) },
                                { SatelliteOrbitNode(title = "Server Component", size = 88.dp) },
                                { SatelliteOrbitNode(title = "v0.dev", size = 88.dp) },
                                { SatelliteOrbitNode(title = "Tailwind CSS", size = 88.dp) },
                                { SatelliteOrbitNode(title = "Image Optimization", size = 88.dp) },
                            )
                        )
                    }
                }
            }
        }
    }
}