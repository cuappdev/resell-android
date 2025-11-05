package com.cornellappdev.resell.android.ui.screens.feedback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.viewmodel.navigation.FeedbackNavigationViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FeedbackNavigation(
    feedbackNavigationViewModel: FeedbackNavigationViewModel = hiltViewModel()
) {
    val uiState = feedbackNavigationViewModel.collectUiStateValue()
    val navController = rememberNavController()

    LaunchedEffect(uiState.route) {
        uiState.route?.consumeSuspend {
            navController.navigate(it)
            feedbackNavigationViewModel.feedbackNavigationRepository.setCanNavigate(true)
        }
    }

    LaunchedEffect(uiState.popBackStack) {
        uiState.popBackStack?.consumeSuspend {
            if (navController.previousBackStackEntry != null) {
                navController.popBackStack()
            }
            feedbackNavigationViewModel.feedbackNavigationRepository.setCanNavigate(true)
        }
    }

    uiState.initialPage?.let {
        NavHost(
            navController = navController,
            startDestination = it,
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
