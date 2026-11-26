package com.cornellappdev.resell.android.ui.components.submitted

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.global.dialog.TwoButtonDialog

@Composable
fun FeedbackSubmittedCard(onDone: () -> Unit) {
    TwoButtonDialog(
        title = stringResource(R.string.feedback_submitted),
        description = stringResource(R.string.feedback_submitted_body),
        primaryButtonText = stringResource(R.string.done),
        secondaryButtonText = null,
        onPrimaryButtonClick = onDone,
        onSecondaryButtonClick = {},
        onDismiss = onDone,
        primaryButtonContainer = ResellTextButtonContainer.PRIMARY,
        primaryButtonState = ResellTextButtonState.ENABLED,
    )
}