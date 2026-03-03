package dev.shoheiyamagiwa.constell.feature.walkthrough

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.walkthrough.composable.ConstellationGraphic
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import dev.shoheiyamagiwa.constell.ui.theme.Blue500
import dev.shoheiyamagiwa.constell.ui.theme.Blue600
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme
import dev.shoheiyamagiwa.constell.ui.theme.Purple600
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate400
import dev.shoheiyamagiwa.constell.ui.theme.Slate700
import dev.shoheiyamagiwa.constell.ui.theme.Slate950
import kotlinx.coroutines.launch

class WalkthroughActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT), navigationBarStyle = SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT))
        setContent {
            ConstellTheme {
                val pagerState = rememberPagerState(pageCount = { walkthroughData.size })
                val coroutineScope = rememberCoroutineScope()

                val currentStep = walkthroughData[pagerState.currentPage]

                fun goToNextPage() {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }

                val hasNextPage = pagerState.currentPage < walkthroughData.lastIndex

                Box(modifier = Modifier.fillMaxSize().background(color = Slate950)) {
                    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                            val skipAlpha by animateFloatAsState(targetValue = if (hasNextPage) 1.0F else 0.0F, animationSpec = tween(durationMillis = 500))

                            TextButton(modifier = Modifier.align(Alignment.TopEnd).graphicsLayer(alpha = skipAlpha).then(if (!hasNextPage) Modifier.clearAndSetSemantics {} else Modifier), enabled = hasNextPage, onClick = { /* TODO */ }) {
                                Text(stringResource(id = R.string.walkthrough_button_skip), color = Slate400, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.fillMaxWidth().weight(1.0F), contentAlignment = Alignment.Center) {
                            ConstellationGraphic(walkthroughStep = currentStep, modifier = Modifier.fillMaxWidth())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(0.75F)) { page ->
                            WalkthroughContent(step = walkthroughData[page])
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            WalkthroughIndicator(state = IndicatorState(currentPage = pagerState.currentPage, totalPages = walkthroughData.size))

                            if (hasNextPage) {
                                IconButton(colors = IconButtonDefaults.iconButtonColors(containerColor = Blue600, contentColor = Color.White), modifier = Modifier.size(56.dp), onClick = { goToNextPage() }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(id = R.string.walkthrough_button_next))
                                }
                            } else {
                                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), contentPadding = PaddingValues(), modifier = Modifier.height(56.dp), onClick = { /* TODO */ }) {
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
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
private fun WalkthroughContent(@PreviewParameter(provider = WalkthroughContentPreviewProvider::class) step: WalkthroughStep, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        if (step.showLogo) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).clip(shape = RoundedCornerShape(size = 12.dp)).background(Brush.linearGradient(colors = listOf(Blue600, Purple600))).padding(all = 8.dp)) {
                Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Constell", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        } else {
            Text(text = stringResource(id = step.titleId), color = Slate100, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(id = step.descriptionId), color = Slate400, fontSize = 15.sp, lineHeight = 24.sp, textAlign = TextAlign.Center)
    }
}

private class WalkthroughContentPreviewProvider : PreviewParameterProvider<WalkthroughStep> {
    override val values: Sequence<WalkthroughStep> = walkthroughData.asSequence()
}

private data class IndicatorState(val currentPage: Int, val totalPages: Int)

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
private fun WalkthroughIndicator(@PreviewParameter(provider = IndicatorStatePreviewProvider::class) state: IndicatorState, modifier: Modifier = Modifier) {
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