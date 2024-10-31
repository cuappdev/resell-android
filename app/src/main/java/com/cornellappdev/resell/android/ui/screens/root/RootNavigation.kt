package com.cornellappdev.resell.android.ui.screens.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.ui.screens.main.ChatScreen
import com.cornellappdev.resell.android.ui.screens.main.MainTabNavigation
import com.cornellappdev.resell.android.ui.screens.main.NotificationsHubScreen
import com.cornellappdev.resell.android.ui.screens.newpost.NewPostNavigation
import com.cornellappdev.resell.android.ui.screens.newpost.RequestDetailsEntryScreen
import com.cornellappdev.resell.android.ui.screens.onboarding.LandingScreen
import com.cornellappdev.resell.android.ui.screens.onboarding.OnboardingNavigation
import com.cornellappdev.resell.android.ui.screens.pdp.PostDetailPage
import com.cornellappdev.resell.android.ui.screens.reporting.ReportNavigation
import com.cornellappdev.resell.android.ui.screens.settings.SettingsNavigation
import com.cornellappdev.resell.android.util.LocalRootNavigator
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavigation(
    rootNavigationViewModel: RootNavigationViewModel = hiltViewModel(),
) {
    val uiState = rootNavigationViewModel.collectUiStateValue()
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var lastSheetValue: RootSheet by remember {
        mutableStateOf(RootSheet.LoginCornellEmail)
    }
    val chatViewModel: ChatViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.sheetEvent) {
        uiState.sheetEvent?.consumeSuspend {
            // Show bottom sheet.
            lastSheetValue = uiState.sheetEvent.payload
            showBottomSheet = true
            sheetState.show()
        }
    }

    LaunchedEffect(uiState.hideEvent) {
        uiState.hideEvent?.consumeSuspend {
            // Hide bottom sheet.
            coroutineScope.launch {
                sheetState.hide()
                showBottomSheet = false
            }
        }
    }

    LaunchedEffect(uiState.navEvent) {
        uiState.navEvent?.consumeSuspend {
            // Navigate.
            navController.navigate(it)
        }
    }

    CompositionLocalProvider(
        LocalRootNavigator provides navController
    ) {
        NavHost(
            navController = LocalRootNavigator.current,
            startDestination = ResellRootRoute.LANDING,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            composable<ResellRootRoute.LANDING> {
                LandingScreen()
            }

            composable<ResellRootRoute.MAIN> {
                MainTabNavigation()
            }

            composable<ResellRootRoute.ONBOARDING> {
                OnboardingNavigation()
            }

            composable<ResellRootRoute.SETTINGS> {
                SettingsNavigation()
            }

            composable<ResellRootRoute.NEW_POST> {
                NewPostNavigation()
            }

            composable<ResellRootRoute.NEW_REQUEST> {
                RequestDetailsEntryScreen()
            }

            composable<ResellRootRoute.PDP> {
                PostDetailPage()
            }

            composable<ResellRootRoute.CHAT> {
                ChatScreen(chatViewModel)
            }
            
            composable<ResellRootRoute.REPORT> {
                ReportNavigation()
            }

            composable <ResellRootRoute.NOTIFICATIONS>{
                NotificationsHubScreen()
            }
        }

        RootConfirmationOverlay()

        RootSheetOverlay(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            sheetType = lastSheetValue,
            showBottomSheet = showBottomSheet,
        )

        RootOptionsMenuOverlay()

        RootDialogOverlay()
    }
}


@Serializable
sealed class ResellRootRoute {
    @Serializable
    data object LANDING : ResellRootRoute()

    @Serializable
    data object MAIN : ResellRootRoute()

    @Serializable
    data object ONBOARDING : ResellRootRoute()

    @Serializable
    data object SETTINGS : ResellRootRoute()

    @Serializable
    data object NEW_POST : ResellRootRoute()

    @Serializable
    data object NEW_REQUEST : ResellRootRoute()

    @Serializable
    data class PDP(
        val id: String,
        val title: String,
        val images: List<String>,
        val price: String,
        val categories: List<String>,
        val description: String,
        val userImageUrl: String,
        val username: String,
        val userId: String,
        val userHumanName: String
    ) : ResellRootRoute()

    @Serializable
    data object CHAT : ResellRootRoute()

    @Serializable
    data class REPORT(
        val reportPost: Boolean,
        val postId: String,
        val userId: String
    ) : ResellRootRoute()

    @Serializable
    data object NOTIFICATIONS : ResellRootRoute()
}
