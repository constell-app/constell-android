package dev.shoheiyamagiwa.constell

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shoheiyamagiwa.constell.feature.auth.AuthScreen
import dev.shoheiyamagiwa.constell.feature.home.HomeScreen
import dev.shoheiyamagiwa.constell.feature.walkthrough.composable.WalkthroughScreen
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme

public class MainActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT), navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
        setContent {
            ConstellTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationDeclaration(isLoggedIn = false, isFirstLaunch = true)
                }
            }
        }
    }
}

@Composable
private fun NavigationDeclaration(isLoggedIn: Boolean, isFirstLaunch: Boolean) {
    val navController = rememberNavController()

    val startDestination = when {
        isFirstLaunch -> Walkthrough
        !isLoggedIn -> Auth
        else -> Home
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Home> {
            HomeScreen()
        }
        composable<Walkthrough> {
            WalkthroughScreen(onSkip = { }, onFinish = { })
        }
        composable<Auth> {
            AuthScreen()
        }
    }
}