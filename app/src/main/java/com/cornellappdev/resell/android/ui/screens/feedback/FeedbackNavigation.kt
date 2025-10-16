package com.cornellappdev.resell.android.ui.screens.feedback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.viewmodel.navigation.FeedbackNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun FeedbackNavigation(
    feedbackNavigationViewModel: FeedbackNavigationViewModel = hiltViewModel()
) {
    val uiState = feedbackNavigationViewModel.collectUiStateValue()
    val navController = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            navController.navigate(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = uiState.initialPage ?: FeedbackScreen.Reason(
            postId = "",
            userId = "",
            userName = "",
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        composable<FeedbackScreen.Reason> {
            FeedbackReasonScreen()
        }

        composable<FeedbackScreen.Details> {
            FeedbackDetailsScreen()
        }
    }
}

@Serializable
sealed class FeedbackScreen {
    @Serializable
    data class Reason(
        val postId: String,
        val userId: String,
        val userName: String,
    ) : FeedbackScreen()

    @Serializable
    data class Details(
        val postId: String,
        val userId: String,
        val reason: String,
        val userName: String,
    ) : FeedbackScreen()
}
