package dev.shoheiyamagiwa.constell

import android.content.Intent
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shoheiyamagiwa.constell.feature.auth.AuthScreen
import dev.shoheiyamagiwa.constell.feature.home.HomeScreen
import dev.shoheiyamagiwa.constell.feature.walkthrough.composable.WalkthroughScreen
import dev.shoheiyamagiwa.constell.ui.theme.ConstellTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

public class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            handleSharedUrl(intent)
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT)
        )

        setContent {
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
                                viewModel.updateFirstLaunch(isFirstLaunch = false)
                            },
                            onLoginSuccess = {
                                viewModel.updateLoggedIn(isLoggedIn = true)
                            }
                        )
                    }

                    MainUiState.Loading -> {}
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleSharedUrl(intent)
    }

    private fun handleSharedUrl(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            // FIXME: We need to check if the shared text is valid URL or not
            val articleUrl = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (articleUrl != null) {
                viewModel.saveSharedArticle(articleUrl = articleUrl)
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
            HomeScreen(onNavigateToAuthScreen = {
                navController.navigate(route = Auth) {
                    popUpTo(route = Home) { inclusive = true }
                }
            })
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
