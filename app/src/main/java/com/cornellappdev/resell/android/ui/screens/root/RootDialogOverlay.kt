package com.cornellappdev.resell.android.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.dialog.CorrectAnswerDialog
import com.cornellappdev.resell.android.ui.components.global.dialog.DialogWrapper
import com.cornellappdev.resell.android.ui.components.global.dialog.TwoButtonDialog
import com.cornellappdev.resell.android.ui.components.submitted.ConfettiOverlay
import com.cornellappdev.resell.android.ui.components.submitted.FeedbackSubmittedCard
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogViewModel
import com.cornellappdev.resell.android.viewmodel.submitted.ConfettiViewModel

@Composable
fun RootDialogOverlay(
    rootDialogViewModel: RootDialogViewModel = hiltViewModel(),
    confettiViewModel: ConfettiViewModel = hiltViewModel()
) {
    val uiState = rootDialogViewModel.collectUiStateValue()

    val isConfettiDialog = uiState.content is RootDialogContent.ReviewSubmittedDialog

    val showPercent by animateFloatAsState(
        if (uiState.showing) 1f else 0f,
        label = "dialog shade"
    )

    if (uiState.content == null || showPercent == 0f) {
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(showPercent)
            .clickableNoIndication {
                rootDialogViewModel.onDismiss()
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(showPercent)
                .background(Color.Black.copy(alpha = showPercent * 0.3f))
        )

        DialogWrapper(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(
                    showPercent * 0.5f + 0.5f
                )
        ) {
            when (uiState.content) {
                is RootDialogContent.TwoButtonDialog -> {
                    TwoButtonDialog(
                        title = uiState.content.title,
                        description = uiState.content.description,
                        primaryButtonText = uiState.content.primaryButtonText,
                        secondaryButtonText = uiState.content.secondaryButtonText,
                        onPrimaryButtonClick = uiState.content.onPrimaryButtonClick,
                        onSecondaryButtonClick = uiState.content.onSecondaryButtonClick,
                        onDismiss = {
                            rootDialogViewModel.onDismiss()
                        },
                        primaryButtonContainer = uiState.content.primaryButtonContainer,
                        secondaryButtonContainer = uiState.content.secondaryButtonContainer,
                        primaryButtonState = uiState.content.primaryButtonState,
                        secondaryButtonState = uiState.content.secondaryButtonState,
                    )
                }

                is RootDialogContent.CorrectAnswerDialog -> {
                    CorrectAnswerDialog(
                        title = uiState.content.title,
                        description = uiState.content.description,
                        onDismiss = {
                            rootDialogViewModel.onDismiss()
                        },
                        correctAnswer = uiState.content.correctAnswer,
                        primaryButtonText = uiState.content.primaryButtonText,
                        secondaryButtonText = uiState.content.secondaryButtonText,
                        onPrimaryButtonClick = uiState.content.onPrimaryButtonClick,
                        onSecondaryButtonClick = uiState.content.onSecondaryButtonClick,
                        primaryButtonContainer = uiState.content.primaryButtonContainer,
                        secondaryButtonContainer = uiState.content.secondaryButtonContainer,
                        primaryButtonState = uiState.content.primaryButtonState,
                        secondaryButtonState = uiState.content.secondaryButtonState
                    )
                }

                is RootDialogContent.ReviewSubmittedDialog -> {
                    FeedbackSubmittedCard(onDone = { rootDialogViewModel.onDismiss() })
                }
            }
        }

        LaunchedEffect(isConfettiDialog) {
            if (isConfettiDialog) {
                confettiViewModel.onShow()
            }
        }

        if (isConfettiDialog) {
            ConfettiOverlay(
                modifier = Modifier.fillMaxSize(),
                confettiViewModel = confettiViewModel
            )
        }
    }

}
