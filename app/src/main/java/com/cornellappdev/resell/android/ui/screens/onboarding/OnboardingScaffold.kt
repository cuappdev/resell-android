package com.cornellappdev.resell.android.ui.screens.onboarding

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
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
        transitionSpec = {
            fadeIn(
                animationSpec = tween(300, delayMillis = 0)
            ).togetherWith(
                fadeOut(
                    animationSpec = tween(300)
                )
            )
        },
        modifier = Modifier.fillMaxSize()
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
