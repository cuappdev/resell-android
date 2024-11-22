package com.cornellappdev.resell.android.ui.screens.root

import android.Manifest
import android.os.Build
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.MainActivity
import com.cornellappdev.resell.android.ui.screens.externalprofile.ExternalProfileNavigation
import com.cornellappdev.resell.android.ui.screens.main.AllSearchScreen
import com.cornellappdev.resell.android.ui.screens.main.ChatScreen
import com.cornellappdev.resell.android.ui.screens.main.MainTabNavigation
import com.cornellappdev.resell.android.ui.screens.main.RequestMatchesScreen
import com.cornellappdev.resell.android.ui.screens.newpost.NewPostNavigation
import com.cornellappdev.resell.android.ui.screens.newpost.RequestDetailsEntryScreen
import com.cornellappdev.resell.android.ui.screens.onboarding.LandingScreen
import com.cornellappdev.resell.android.ui.screens.onboarding.OnboardingNavigation
import com.cornellappdev.resell.android.ui.screens.pdp.PostDetailPage
import com.cornellappdev.resell.android.ui.screens.reporting.ReportNavigation
import com.cornellappdev.resell.android.ui.screens.settings.SettingsNavigation
import com.cornellappdev.resell.android.util.LocalInfiniteLoading
import com.cornellappdev.resell.android.util.LocalRootNavigator
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
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Create an infinite transition for animation
    val transition = rememberInfiniteTransition()

    val context = LocalContext.current as? MainActivity

    // Animate a value from 0 to 1 infinitely
    val animatedValue = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                1f at 1000
                0f at 2000
            }
        ),
        label = "infinite loading"
    ).value

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

    LaunchedEffect(uiState.popBackStack) {
        uiState.popBackStack?.consumeSuspend {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.requestNotificationPermissions) {
        uiState.requestNotificationPermissions?.consumeSuspend {
            if (context == null) return@consumeSuspend

            context.askNotificationPermission(
                onAlreadyGranted = rootNavigationViewModel::onPermissionsAlreadyGranted,
                onShowUi = rootNavigationViewModel::onShowRationaleUi,
            )
        }
    }

    LaunchedEffect(uiState.directlyRequestNotificationPermissions) {
        uiState.directlyRequestNotificationPermissions?.consumeSuspend {
            if (context == null) return@consumeSuspend

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    CompositionLocalProvider(
        LocalRootNavigator provides navController,
        LocalInfiniteLoading provides animatedValue
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
                ChatScreen()
            }

            composable<ResellRootRoute.REPORT> {
                ReportNavigation()
            }

            composable<ResellRootRoute.EXTERNAL_PROFILE> {
                ExternalProfileNavigation()
            }

            composable<ResellRootRoute.SEARCH> {
                AllSearchScreen()
            }

            composable<ResellRootRoute.REQUEST_MATCHES> {
                RequestMatchesScreen()
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
    data class CHAT(
        /** Real name.*/
        val name: String,
        val email: String,
        val pfp: String,
        // TODO There should be some way to fix this automatically but I can't figure it out.
        val postJson: String,
        val isBuyer: Boolean
    ) : ResellRootRoute()

    @Serializable
    data class REPORT(
        val reportPost: Boolean,
        val postId: String,
        val userId: String
    ) : ResellRootRoute()

    @Serializable
    data class EXTERNAL_PROFILE(
        val id: String
    ) : ResellRootRoute()

    @Serializable
    data object SEARCH : ResellRootRoute()

    @Serializable
    data class REQUEST_MATCHES(
        val id: String,
        val title: String,
    ) : ResellRootRoute()
}
