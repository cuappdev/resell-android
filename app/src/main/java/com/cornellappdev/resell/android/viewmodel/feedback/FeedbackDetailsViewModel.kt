package com.cornellappdev.resell.android.viewmodel.feedback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.settings.SettingsRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.feedback.FeedbackScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.FeedbackNavigationRepository
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.submitted.ConfettiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val feedbackNavigationRepository: FeedbackNavigationRepository,
    private val confettiRepository: ConfettiRepository
) :
    ResellViewModel<FeedbackDetailsViewModel.FeedbackDetailsUiState>(
        initialUiState = FeedbackDetailsUiState(
            reason = "",
            typedContent = ""
        )
    ) {

    data class FeedbackDetailsUiState(
        val loadingSubmit: Boolean = false,
        val reason: String,
        val typedContent: String,
        val postId: String = "",
        val userId: String = "",
        val userName: String = ""
    ) {
        val title: String
            get() = "Submit Feedback"

        val subtitle: String
            get() = "Explained what happened:"

        val body: String
            get() = "Describe any issues that occurred during your transaction with $userName."

        val buttonState
            get() = if (loadingSubmit) {
                ResellTextButtonState.SPINNING
            } else if (typedContent.isEmpty()) {
                ResellTextButtonState.DISABLED
            } else {
                ResellTextButtonState.ENABLED
            }
    }

    init {
        val navArgs = savedStateHandle.toRoute<FeedbackScreen.Details>()

        applyMutation {
            copy(
                reason = navArgs.reason,
                postId = navArgs.postId,
                userId = navArgs.userId,
                userName = navArgs.userName
            )
        }
    }

    fun onTypedContentChanged(typedContent: String) {
        applyMutation {
            copy(
                typedContent = typedContent
            )
        }
    }

    fun onBackArrow() {
        feedbackNavigationRepository.popBackStack()
    }

    fun onSubmitPressed() {
        viewModelScope.launch {
            applyMutation {
                copy(
                    loadingSubmit = true
                )
            }

            // TODO add networking for feedback submission
            // Navigate back to the home page and show the review submitted dialog
            rootNavigationRepository.navigate(
                ResellRootRoute.MAIN
            )

            delay(100)

            rootDialogRepository.showDialog(
                RootDialogContent.ReviewSubmittedDialog(
                    onDismiss = { rootDialogRepository.dismissDialog() }
                )
            )

            confettiRepository.showConfetti()
        }
    }
}
