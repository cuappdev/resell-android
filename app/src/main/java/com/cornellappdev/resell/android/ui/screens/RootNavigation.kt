package com.cornellappdev.resell.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.main.ShadeOverlay
import com.cornellappdev.resell.android.ui.screens.main.MainTabNavigation
import com.cornellappdev.resell.android.ui.screens.onboarding.LandingScreen
import com.cornellappdev.resell.android.ui.screens.onboarding.OnboardingNavigation
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.LocalRootNavigator
import com.cornellappdev.resell.android.viewmodel.RootNavigationViewModel
import com.cornellappdev.resell.android.viewmodel.RootSheet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavigation(
    rootNavigationViewModel: RootNavigationViewModel = hiltViewModel(),
) {
    val uiState = rootNavigationViewModel.collectUiStateValue()
    val sheetState = rememberModalBottomSheetState()
    var lastSheetValue by remember {
        mutableStateOf(RootSheet.LOGIN_CORNELL_EMAIL)
    }
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

    CompositionLocalProvider(
        LocalRootNavigator provides rootNavigationViewModel.navController
    ) {
        NavHost(
            navController = LocalRootNavigator.current,
            startDestination = ResellRootRoute.LOGIN,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            composable<ResellRootRoute.LOGIN> {
                LandingScreen()
            }

            composable<ResellRootRoute.MAIN> {
                MainTabNavigation()
            }

            composable<ResellRootRoute.ONBOARDING> {
                OnboardingNavigation()
            }

            composable<ResellRootRoute.SETTINGS> {
                // TODO: Settings
                Text(text = "SETTINGS")
            }
        }

        SheetOverlay(
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetOverlay(
    sheetState: SheetState,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    sheetType: RootSheet,
) {
    // Convert to dp
    val density = LocalDensity.current

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            windowInsets = WindowInsets(0.dp),
        ) {

            when (sheetType) {
                RootSheet.LOGIN_CORNELL_EMAIL -> {
                    LoginErrorSheet(
                        onTryAgainClicked = onDismissRequest
                    )
                }

                RootSheet.LOGIN_FAILED -> {
                    LoginErrorSheet(
                        onTryAgainClicked = onDismissRequest,
                        text = "Login failed.\nPlease try again."
                    )
                }

                else -> {
                    TODO("Not implemented yet")
                }
            }

            // Bottom padding to account for bottom bars.
            with(density) {
                Spacer(
                    modifier = Modifier.padding(
                        bottom = BottomSheetDefaults.windowInsets.getBottom(
                            density
                        ).toDp()
                    )
                )
            }
        }
    }
}

@Composable
private fun LoginErrorSheet(
    text: String = "Please sign in with\na Cornell email",
    onTryAgainClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(top = 24.dp, bottom = 36.dp),
            style = Style.heading3,
            textAlign = TextAlign.Center
        )

        ResellTextButton(
            text = "Try Again", onClick = onTryAgainClicked,
            modifier = Modifier.padding(bottom = 24.dp)
        )
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

    @Serializable
    data object SETTINGS : ResellRootRoute()
}
