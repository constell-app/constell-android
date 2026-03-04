package dev.shoheiyamagiwa.constell.feature.walkthrough.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.walkthrough.WalkthroughViewModel
import dev.shoheiyamagiwa.constell.feature.walkthrough.walkthroughViewModelFactory
import dev.shoheiyamagiwa.constell.ui.theme.Blue600
import dev.shoheiyamagiwa.constell.ui.theme.Purple600
import dev.shoheiyamagiwa.constell.ui.theme.Slate400
import dev.shoheiyamagiwa.constell.ui.theme.Slate950
import kotlinx.coroutines.launch

@Composable
public fun WalkthroughScreen(modifier: Modifier = Modifier, viewModel: WalkthroughViewModel = viewModel(factory = walkthroughViewModelFactory), onSkip: () -> Unit, onFinish: () -> Unit) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val totalSteps = viewModel.getTotalSteps()

    val pagerState = rememberPagerState(pageCount = { totalSteps })
    val coroutineScope = rememberCoroutineScope()

    val hasNextPage = pagerState.currentPage < totalSteps - 1

    fun goToNextPage() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            viewModel.updateStep(pageIndex)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color = Slate950)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                val skipAlpha by animateFloatAsState(targetValue = if (hasNextPage) 1.0F else 0.0F, animationSpec = tween(durationMillis = 500))

                TextButton(modifier = Modifier.align(Alignment.TopEnd).graphicsLayer(alpha = skipAlpha).then(if (!hasNextPage) Modifier.clearAndSetSemantics {} else Modifier), enabled = hasNextPage, onClick = onSkip) {
                    Text(stringResource(id = R.string.walkthrough_button_skip), color = Slate400, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1.0F), contentAlignment = Alignment.Center) {
                ConstellationGraphic(walkthroughStep = currentStep, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(0.75F)) { page ->
                WalkthroughContent(step = viewModel.getStepByPage(page))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                WalkthroughIndicator(state = IndicatorState(currentPage = pagerState.currentPage, totalPages = totalSteps))

                if (hasNextPage) {
                    IconButton(colors = IconButtonDefaults.iconButtonColors(containerColor = Blue600, contentColor = Color.White), modifier = Modifier.size(56.dp), onClick = { goToNextPage() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(id = R.string.walkthrough_button_next))
                    }
                } else {
                    Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), contentPadding = PaddingValues(), modifier = Modifier.height(56.dp), onClick = onFinish) {
                        Box(modifier = Modifier.fillMaxHeight().background(Brush.horizontalGradient(colors = listOf(Blue600, Purple600)), shape = RoundedCornerShape(size = 28.dp)).padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(id = R.string.walkthrough_button_start), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(id = R.string.walkthrough_button_start), tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}