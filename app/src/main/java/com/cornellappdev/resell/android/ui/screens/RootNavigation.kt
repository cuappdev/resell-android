package com.cornellappdev.resell.android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.ui.screens.main.MainTabScaffold
import com.cornellappdev.resell.android.ui.screens.onboarding.LandingScreen
import com.cornellappdev.resell.android.util.LocalNavController
import kotlinx.serialization.Serializable

@Composable
fun RootNavigation() {
    val navController: NavHostController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = LocalNavController.current,
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
