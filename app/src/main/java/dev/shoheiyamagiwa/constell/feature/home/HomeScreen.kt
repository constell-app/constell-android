package dev.shoheiyamagiwa.constell.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shoheiyamagiwa.constell.composable.Background
import dev.shoheiyamagiwa.constell.feature.home.composable.ConstellationWorld
import dev.shoheiyamagiwa.constell.feature.home.composable.extensions.dotBackground
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate400
import dev.shoheiyamagiwa.constell.ui.theme.Slate600
import dev.shoheiyamagiwa.constell.ui.theme.Slate800
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    val onAction: (HomeAction) -> Unit = remember(key1 = viewModel) {
        { action ->
            when (action) {
                is HomeAction.Initialize -> viewModel.initializeScreen()
                is HomeAction.SelectArticle -> viewModel.selectArticleById(articleId = action.id)
                is HomeAction.SetShowDetails -> viewModel.setShowArticleDetails(action.show)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        onAction(HomeAction.Initialize)
    }

    HomeScreenContent(screenState = screenState, sheetState = sheetState, onAction = onAction)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
public fun HomeScreenContent(screenState: HomeScreenState, sheetState: SheetState, onAction: (HomeAction) -> Unit) {
    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        when (screenState) {
            is HomeScreenState.Loading -> {
                LoadingContent()
            }

            is HomeScreenState.Default -> {
                DefaultContent(uiState = screenState, sheetState = sheetState, onAction = onAction)
            }

            is HomeScreenState.Error -> {
                ErrorContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContent() {
    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultContent(uiState: HomeScreenState.Default, sheetState: SheetState, onAction: (HomeAction) -> Unit) {
    if (uiState.mainArticleNode == null) {
        EmptyContent()
    } else {
        val mainNode = uiState.mainArticleNode

        ConstellationWorld(
            nodeGap = 144.dp,
            edgeWidth = 2.dp,
            centerNodeTitle = mainNode.title,
            onMainNodeClicked = {
                onAction(HomeAction.SetShowDetails(show = true))
            },
            satelliteNodeTitles = mainNode.similarArticles.map { it.title },
            onSatelliteNodeClicked = { index ->
                mainNode.similarArticles.getOrNull(index)?.id?.let { id ->
                    onAction(HomeAction.SelectArticle(id))
                }
            },
            isFocusing = uiState.showArticleDetails
        )

        if (uiState.showArticleDetails) {
            ArticleDetailSheet(
                articleNode = mainNode,
                sheetState = sheetState,
                onDismissRequest = { onAction(HomeAction.SetShowDetails(show = false)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyContent() {
    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            // TODO: We must create an empty screen design to render this state
            Text(text = "No articles found", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContent() {
    Background(modifier = Modifier.dotBackground(dotColor = Slate600, dotRadius = 1.5.dp, spacing = 30.dp, alpha = 0.3F)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            // TODO: We must create an error screen design to render this state
            Text(text = "An error has occurred", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
public fun ArticleDetailSheet(articleNode: ArticleNode, sheetState: SheetState, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Slate800,
        contentColor = Slate100,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 48.dp)) {
            Text(text = articleNode.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(modifier = Modifier.fillMaxWidth(), maxItemsInEachRow = 5) {
                articleNode.tags.forEach { tag ->
                    Surface(modifier = Modifier.padding(end = 8.dp, bottom = 8.dp), color = Slate600, shape = RoundedCornerShape(size = 16.dp)) {
                        Text(text = "#$tag", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, color = Slate100)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = articleNode.description, fontSize = 16.sp, lineHeight = 24.sp, color = Slate400)
        }
    }
}