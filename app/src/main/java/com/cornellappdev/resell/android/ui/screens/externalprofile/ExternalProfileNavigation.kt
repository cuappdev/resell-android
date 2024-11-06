package com.cornellappdev.resell.android.ui.screens.externalprofile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.viewmodel.externalprofile.ExternalNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun ExternalProfileNavigation(
    viewModel: ExternalNavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState = viewModel.collectUiStateValue()

    LaunchedEffect(uiState.route) {
        uiState.route?.consume {
            navController.navigate(it)
        }
    }

    LaunchedEffect(uiState.popBackStack) {
        uiState.popBackStack?.consume {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = uiState.startingRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<ExternalProfileRoute.ExternalProfile> {
            ExternalProfileScreen()
        }

        composable<ExternalProfileRoute.Search> {
            ExternalProfileSearchScreen()
        }
    }
}

@Serializable
sealed class ExternalProfileRoute {
    @Serializable
    data class ExternalProfile(
        val uid: String
    ) : ExternalProfileRoute()

    @Serializable
    data class Search(
        val uid: String,
        val username: String,
    ) : ExternalProfileRoute()
}
