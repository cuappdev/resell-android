package com.cornellappdev.resell.android.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.util.LocalNavigator

@Composable
fun OnboardingScaffold() {
    val selectedScreen: MutableState<ResellOnboardingScreen> =
        remember { mutableStateOf(ResellOnboardingScreen.Setup) }
    val navigator = LocalNavigator.current

    BackHandler {
        if (selectedScreen.value == ResellOnboardingScreen.Setup) {
            navigator.navigate(ResellRootRoute.LOGIN)
        } else {
            selectedScreen.value = ResellOnboardingScreen.Setup
        }
    }

    AnimatedContent(
        targetState = selectedScreen.value,
        label = "screen switch",
        transitionSpec = simpleFadeInOut,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.systemBars
            )
    ) { state ->
        when (state) {
            ResellOnboardingScreen.Setup -> SetupScreen {
                selectedScreen.value = ResellOnboardingScreen.Venmo
            }

            ResellOnboardingScreen.Venmo -> VenmoFieldScreen(
                onNavigateProceed = {
                    navigator.navigate(ResellRootRoute.MAIN)
                }
            )
        }
    }
}

sealed class ResellOnboardingScreen {

    data object Setup : ResellOnboardingScreen()
    data object Venmo : ResellOnboardingScreen()
}
