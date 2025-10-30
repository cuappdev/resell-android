package com.cornellappdev.resell.android.ui.components.submitted

import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.global.dialog.TwoButtonDialog

@Composable
fun FeedbackSubmittedCard(onDone: () -> Unit) {
    TwoButtonDialog(
        title = "Feedback Submitted",
        description = "Thank you for being a part of the Resell experience!",
        primaryButtonText = "Done",
        secondaryButtonText = null,
        onPrimaryButtonClick = onDone,
        onSecondaryButtonClick = {},
        onDismiss = onDone,
        primaryButtonContainer = ResellTextButtonContainer.PRIMARY,
        primaryButtonState = ResellTextButtonState.ENABLED,
    )
}