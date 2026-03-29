package dev.shoheiyamagiwa.constell.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.feature.home.composable.ConstellationWorld
import dev.shoheiyamagiwa.constell.feature.home.composable.extensions.dotBackground
import dev.shoheiyamagiwa.constell.ui.theme.Slate600
import org.koin.compose.viewmodel.koinViewModel

@Composable
public fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeScreen()
    }

    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        when (val currentState = screenState) {
            is HomeScreenState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            is HomeScreenState.Default -> {
                if (currentState.mainArticleNode == null) {
                    Text(text = "No articles found.")
                } else {
                    val mainNode = currentState.mainArticleNode
                    ConstellationWorld(
                        nodeGap = 144.dp,
                        edgeWidth = 2.dp,
                        centerNodeTitle = mainNode.title,
                        onMainNodeClicked = { /* TODO */ },
                        satelliteNodeTitles = mainNode.similarArticles.map { it.title },
                        onSatelliteNodeClicked = { nodeId ->
                            /* TODO */
                        },
                        isFocusing = false // FIXME Temporary marked as false
                    )
                }
            }

            is HomeScreenState.Error -> {
                Text(text = "Error: ${currentState.exception.message}")
            }
        }
    }
}