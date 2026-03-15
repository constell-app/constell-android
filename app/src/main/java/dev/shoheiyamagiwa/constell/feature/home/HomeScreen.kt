package dev.shoheiyamagiwa.constell.feature.home

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.feature.home.composable.ConstellationWorld
import dev.shoheiyamagiwa.constell.feature.home.composable.extensions.dotBackground
import dev.shoheiyamagiwa.constell.ui.theme.Slate600

@Composable
public fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeScreen()
    }

    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        when (val currentState = screenState) {
            is HomeScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is HomeScreenState.Default -> {
                ConstellationWorld(nodeGap = 144.dp, edgeWidth = 2.dp, centerNodeTitle = "Next.js", onMainNodeClicked = { TODO() },
                    satelliteNodeTitles = listOf(
                        "React",
                        "Vercel",
                        "Server Component",
                        "v0.dev",
                        "Tailwind CSS",
                        "Image Optimization",
                    ),
                    onSatelliteNodeClicked = { nodeId ->

                    },
                    isFocusing = false // FIXME Temporary marked as false
                )
            }

            is HomeScreenState.Error -> {
                Text(text = "Error: ${currentState.exception.message}")
            }
        }
    }
}