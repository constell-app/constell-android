package dev.shoheiyamagiwa.constell.feature.auth.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.composable.AppLogo
import dev.shoheiyamagiwa.constell.ui.theme.Slate400

@Composable
public fun AuthHeader(modifier: Modifier = Modifier, logo: @Composable () -> Unit, title: String, subtitle: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        logo()
        Spacer(Modifier.height(24.dp))
        Text(text = title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(text = subtitle, color = Slate400, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
private fun AuthHeaderPreview() {
    AuthHeader(
        logo = { AppLogo() },
        title = "Welcome to Constell",
        subtitle = "Your AI Second Brain"
    )
}