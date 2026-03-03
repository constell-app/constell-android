package dev.shoheiyamagiwa.constell.feature.walkthrough.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import dev.shoheiyamagiwa.constell.ui.theme.Blue600
import dev.shoheiyamagiwa.constell.ui.theme.Purple600
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate400

@Composable
public fun WalkthroughContent(step: WalkthroughStep, modifier: Modifier = Modifier) {
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