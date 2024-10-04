package com.cornellappdev.resell.android.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.LocalOnboardingNavigator
import com.cornellappdev.resell.android.util.LocalRootNavigator
import com.cornellappdev.resell.android.viewmodel.navigation.OnboardingNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun OnboardingNavigation(
    onboardingNavigationViewModel: OnboardingNavigationViewModel = hiltViewModel()
) {
    val selectedScreen: MutableState<ResellOnboardingScreen> =
        remember { mutableStateOf(ResellOnboardingScreen.Setup) }
    val navigator = LocalRootNavigator.current
    val onboardingNav = onboardingNavigationViewModel.onboardingNavController

    BackHandler {
        if (selectedScreen.value == ResellOnboardingScreen.Setup) {
            navigator.navigate(ResellRootRoute.LOGIN)
        } else {
            selectedScreen.value = ResellOnboardingScreen.Setup
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
    data object Venmo : ResellOnboardingScreen()
}
