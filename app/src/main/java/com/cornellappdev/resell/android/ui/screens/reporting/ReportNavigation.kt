package com.cornellappdev.resell.android.ui.screens.reporting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationViewModel
import com.cornellappdev.resell.android.viewmodel.report.ReportType
import kotlinx.serialization.Serializable

@Composable
fun ReportNavigation(
    reportNavigationViewModel: ReportNavigationViewModel = hiltViewModel()
) {
    val uiState = reportNavigationViewModel.collectUiStateValue()
    val navController = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            navController.navigate(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = uiState.initialPage ?: ReportScreen.Reason(
            reportType = ReportType.POST,
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

@Serializable
sealed class ReportScreen {
    @Serializable
    data class Reason(
        val reportType: ReportType,
        val postId: String,
        val userId: String
    ) : ReportScreen()

    @Serializable
    data class Details(
        val reportType: ReportType,
        val postId: String,
        val userId: String,
        val reason: String,
    ) : ReportScreen()

    @Serializable
    data class Confirmation(
        val reportType: ReportType,
        val userId: String,
    ) : ReportScreen()
}
