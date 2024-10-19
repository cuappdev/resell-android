package com.cornellappdev.resell.android.ui.screens.reporting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.util.LocalReportNavigator
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun ReportNavigation(
    reportNavigationViewModel: ReportNavigationViewModel = hiltViewModel()
) {
    val uiState = reportNavigationViewModel.collectUiStateValue()
    val onboardingNav = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            onboardingNav.navigate(it)
        }
    }

    CompositionLocalProvider(LocalReportNavigator provides onboardingNav) {
        NavHost(
            navController = LocalReportNavigator.current,
            startDestination = ReportScreen.Reason,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ReportScreen.Reason> {
            }

            composable<ReportScreen.Details> {
            }

            composable<ReportScreen.Confirmation> {

            }
        }
    }
}

@Serializable
sealed class ReportScreen {
    @Serializable
    data object Reason : ReportScreen()

    @Serializable
    data object Details : ReportScreen()

    @Serializable
    data object Confirmation : ReportScreen()
}
