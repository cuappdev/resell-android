package com.cornellappdev.resell.android.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.util.LocalSettingsNavigator
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun SettingsNavigation(
    settingsNavigationViewModel: SettingsNavigationViewModel = hiltViewModel()
) {
    val uiState = settingsNavigationViewModel.collectUiStateValue()
    val navController = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            navController.navigate(it)
        }
    }

    CompositionLocalProvider(LocalSettingsNavigator provides navController) {
        NavHost(
            navController = LocalSettingsNavigator.current,
            startDestination = SettingsRoute.SettingsLanding
        ) {
            composable<SettingsRoute.SettingsLanding> {
                SettingsLandingScreen()
            }
            composable<SettingsRoute.EditProfile> {
            }
            composable<SettingsRoute.Notifications> {
                NotificationSettings()
            }
            composable<SettingsRoute.Feedback> {
                SendFeedbackScreen()
            }
            composable<SettingsRoute.BlockedUsers> {

            }
        }
    }
}

@Serializable
sealed class SettingsRoute {
    @Serializable
    data object SettingsLanding : SettingsRoute()

    @Serializable
    data object EditProfile : SettingsRoute()

    @Serializable
    data object Notifications : SettingsRoute()

    @Serializable
    data object Feedback : SettingsRoute()

    @Serializable
    data object BlockedUsers : SettingsRoute()
}
