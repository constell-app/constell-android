package dev.shoheiyamagiwa.constell.feature.walkthrough

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.shoheiyamagiwa.constell.feature.walkthrough.composable.WalkthroughScreen
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme

class WalkthroughActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT), navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
        setContent {
            ConstellTheme {
                WalkthroughScreen(onSkip = {}, onFinish = {})
            }
        }
    }
}