package com.cornellappdev.resell.android.ui.screens.root

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.availability.AvailabilitySheet
import com.cornellappdev.resell.android.ui.components.global.sheet.ChatMeetingSheet
import com.cornellappdev.resell.android.ui.components.global.sheet.LoginErrorSheet
import com.cornellappdev.resell.android.ui.components.global.sheet.PriceProposalSheet
import com.cornellappdev.resell.android.ui.components.global.sheet.TwoButtonSheet
import com.cornellappdev.resell.android.ui.components.main.WelcomeSheetContent
import com.cornellappdev.resell.android.ui.components.settings.LogOutSheetContent
import com.cornellappdev.resell.android.ui.components.settings.ResellWebView
import com.cornellappdev.resell.android.viewmodel.root.RootSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootSheetOverlay(
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
            contentWindowInsets = { WindowInsets(0.dp) },
            containerColor = Color.White,
        ) {

            when (sheetType) {
                RootSheet.LoginCornellEmail -> {
                    LoginErrorSheet(
                        onTryAgainClicked = onDismissRequest
                    )
                }

                RootSheet.LoginFailed -> {
                    LoginErrorSheet(
                        onTryAgainClicked = onDismissRequest,
                        text = "Login failed.\nPlease try again."
                    )
                }

                is RootSheet.ProposalSheet -> {
                    PriceProposalSheet()
                }

                is RootSheet.WebViewSheet -> {
                    ResellWebView(url = sheetType.url)
                }

                is RootSheet.MeetingCancel -> {
                    ChatMeetingSheet()
                }

                is RootSheet.MeetingDetails -> {
                    ChatMeetingSheet()
                }

                is RootSheet.Availability -> {
                    AvailabilitySheet()
                }

                RootSheet.LogOut -> {
                    LogOutSheetContent {
                        onDismissRequest()
                    }
                }

                RootSheet.Welcome -> {
                    WelcomeSheetContent {
                        onDismissRequest()
                    }
                }

                is RootSheet.TwoButtonSheet -> {
                    TwoButtonSheet()
                }

                else -> {}
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
