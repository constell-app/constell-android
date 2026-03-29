package dev.shoheiyamagiwa.constell

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.auth.AuthScreen
import dev.shoheiyamagiwa.constell.feature.home.HomeScreen
import dev.shoheiyamagiwa.constell.feature.walkthrough.composable.WalkthroughScreen
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel

public class MainActivity : ComponentActivity() {
    private val repository: UserPreferencesRepository by inject()

    public override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT)
        )
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            splashScreen.setKeepOnScreenCondition {
                uiState is MainUiState.Loading
            }

            ConstellTheme {
                when (val state = uiState) {
                    is MainUiState.Success -> {
                        NavigationDeclaration(
                            isLoggedIn = state.isLoggedIn,
                            isFirstLaunch = state.isFirstLaunch,
                            onFinishWalkthrough = {
                                lifecycleScope.launch {
                                    repository.updateFirstLaunch(isFirstLaunch = false)
                                }
                            },
                            onLoginSuccess = {
                                lifecycleScope.launch {
                                    repository.updateLoggedIn(isLoggedIn = true)
                                }
                            }
                        )
                    }

                    MainUiState.Loading -> {}
                }
            }
        }
    }
}

@Composable
private fun NavigationDeclaration(
    isLoggedIn: Boolean,
    isFirstLaunch: Boolean,
    onFinishWalkthrough: () -> Unit,
    onLoginSuccess: () -> Unit
) {
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
            WalkthroughScreen(
                onSkip = {
                    onFinishWalkthrough()
                    navController.navigate(route = Auth) {
                        popUpTo(route = Walkthrough) { inclusive = true }
                    }
                },
                onFinish = {
                    onFinishWalkthrough()
                    navController.navigate(route = Auth) {
                        popUpTo(route = Walkthrough) { inclusive = true }
                    }
                }
            )
        }
        composable<Auth> {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(route = Home) {
                        popUpTo(route = Auth) { inclusive = true }
                    }
                },
                onNavigateToConfirmEmail = {
                    // TODO: Implement this logic
//                    navController.navigate(route = ConfirmEmail) {
//                        popUpTo(route = Auth) { inclusive = true }
//                    }
                }
            )
        }
    }
}