package com.cornellappdev.resell.android.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cornellappdev.resell.android.ui.screens.main.MainTabScaffold
import com.cornellappdev.resell.android.ui.screens.onboarding.LandingScreen
import com.cornellappdev.resell.android.viewmodel.RootNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun RootNavigation(
    rootNavigationViewModel: RootNavigationViewModel = hiltViewModel(),
) {
    NavHost(
        navController = rootNavigationViewModel.navController,
        startDestination = ResellRootRoute.LOGIN,
    ) {
        composable<ResellRootRoute.LOGIN> {
            LandingScreen()
        }

        composable<ResellRootRoute.MAIN> {
            MainTabScaffold()
        }
    }
}

@Serializable
sealed class ResellRootRoute {
    @Serializable
    data object LOGIN : ResellRootRoute()

    @Serializable
    data object MAIN : ResellRootRoute()

    @Serializable
    data object ONBOARDING : ResellRootRoute()
}
