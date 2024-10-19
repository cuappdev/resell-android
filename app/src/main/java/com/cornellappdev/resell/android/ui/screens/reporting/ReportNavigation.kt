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
            startDestination = uiState.initialPage ?: ReportScreen.Reason(
                reportPost = true,
                postId = "",
                userId = ""
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ReportScreen.Reason> {
                ReportReasonScreen()
            }

            composable<ReportScreen.Details> {
                ReportDetailsScreen()
            }

            composable<ReportScreen.Confirmation> {
                ReportConfirmationScreen()
            }
        }
    }
}

@Serializable
sealed class ReportScreen {
    @Serializable
    data class Reason(
        val reportPost: Boolean,
        val postId: String,
        val userId: String
    ) : ReportScreen()

    @Serializable
    data class Details(
        val reportPost: Boolean,
        val postId: String,
        val userId: String,
        val reason: String,
    ) : ReportScreen()

    @Serializable
    data class Confirmation(
        val reportPost: Boolean,
        val userId: String,
    ) : ReportScreen()
}
