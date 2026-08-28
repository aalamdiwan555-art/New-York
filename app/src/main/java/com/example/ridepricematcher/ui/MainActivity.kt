package com.example.ridepricematcher.ui

import android.os.Bundle
import com.example.ridepricematcher.ads.UnityAdsManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ridepricematcher.ui.screens.admin.AdminScreen
import com.example.ridepricematcher.ui.screens.auth.ForgotPasswordScreen
import com.example.ridepricematcher.ui.screens.auth.LoginScreen
import com.example.ridepricematcher.ui.screens.auth.SignupScreen
import com.example.ridepricematcher.ui.screens.home.HomeScreen
import com.example.ridepricematcher.ui.screens.onboarding.OnboardingScreen
import com.example.ridepricematcher.ui.screens.settings.SettingsScreen
import com.example.ridepricematcher.ui.screens.subscription.SubscriptionScreen
import com.example.ridepricematcher.ui.theme.RidePriceMatcherTheme
import com.example.ridepricematcher.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RidePriceMatcherTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UnityAdsManager.showInterstitialIfAllowed(this)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignup = { navController.navigate("signup") },
                onNavigateToForgot = { navController.navigate("forgot") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignupSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("forgot") {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToSubscription = { navController.navigate("subscription") },
                onNavigateToAdmin = { navController.navigate("admin") },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("subscription") {
            SubscriptionScreen(onBack = { navController.popBackStack() })
        }
        composable("admin") {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}
