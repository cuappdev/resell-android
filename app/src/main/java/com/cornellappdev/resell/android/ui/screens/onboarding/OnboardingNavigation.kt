package com.cornellappdev.resell.android.ui.screens.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.util.LocalOnboardingNavigator
import com.cornellappdev.resell.android.viewmodel.navigation.OnboardingNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun OnboardingNavigation(
    onboardingNavigationViewModel: OnboardingNavigationViewModel = hiltViewModel()
) {
    val uiState = onboardingNavigationViewModel.collectUiStateValue()
    val onboardingNav = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            onboardingNav.navigate(it)
        }
    }

    CompositionLocalProvider(LocalOnboardingNavigator provides onboardingNav) {
        NavHost(
            navController = LocalOnboardingNavigator.current,
            startDestination = ResellOnboardingScreen.Setup,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ResellOnboardingScreen.Setup> {
                SetupScreen()
            }

            composable<ResellOnboardingScreen.Venmo> {
                VenmoFieldScreen()
            }
        }
    }
}

@Serializable
sealed class ResellOnboardingScreen {
    @Serializable
    data object Setup : ResellOnboardingScreen()

    @Serializable
    data class Venmo(
        val username: String,
        val bio: String,
    ) : ResellOnboardingScreen()
}
