package com.cornellappdev.resell.android.viewmodel.feedback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.feedback.FeedbackScreen
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.FeedbackNavigationRepository
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackReasonViewModel @Inject constructor(
    private val feedbackNavigationRepository: FeedbackNavigationRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ResellViewModel<FeedbackReasonViewModel.FeedbackReasonUiState>(
        initialUiState = FeedbackReasonUiState
    ) {

    object FeedbackReasonUiState {
        val reasons: List<String>
            get() = FeedbackReason.entries.map { it.message }

        val title: String
            get() = "Submit Feedback"

        val subtitle: String
            get() = "Explain what happened"
    }

    fun onReasonPressed(reason: String) {
        val navArgs = savedStateHandle.toRoute<FeedbackScreen.Reason>()
        feedbackNavigationRepository.navigate(
            FeedbackScreen.Details(
                postId = navArgs.postId,
                userId = navArgs.userId,
                reason = reason,
                userName = navArgs.userName,
            )
        )
    }

    fun onBackArrow() {
        viewModelScope.launch {
            // Small delay to let any pending navigation settle (prevent a blank FeedbackReasonScreen)
            delay(50)
            rootNavigationRepository.popBackStack()
        }
    }
}

enum class FeedbackReason(
    val message: String
) {
    STOPPED_RESPONDING(
        message = "User Stopped Responding"
    ),
    ITEM_PROBLEM(
        message = "Item arrived damaged"
    ),
    ITEM_DIFFERENT(
        message = "Item is different than what I expected"
    ),
    APP_ISSUE(
        message = "There was an error on the app"
    )
}
