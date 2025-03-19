package com.cornellappdev.resell.android.viewmodel.report

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.Report
import com.cornellappdev.resell.android.model.settings.FeedbackRepository
import com.cornellappdev.resell.android.model.settings.SettingsRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportDetailsViewModel @Inject constructor(
    private val reportNavigationRepository: ReportNavigationRepository,
    private val savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val feedbackRepository: FeedbackRepository
) :
    ResellViewModel<ReportDetailsViewModel.ReportDetailsUiState>(
        initialUiState = ReportDetailsUiState(
            reportType = ReportType.POST,
            reason = "",
            typedContent = ""
        )
    ) {


    data class ReportDetailsUiState(
        val reportType: ReportType,
        val loadingSubmit: Boolean = false,
        val reason: String,
        val typedContent: String,
        val postId: String = "",
        val userId: String = "",
        val username: String = "",
    ) {
        val title: String
            get() = when(reportType) {
                ReportType.POST -> "Report Post"
                ReportType.USER -> "Report Account"
                ReportType.POST_TRANSACTION -> "Submit Feedback"
            }

        val body: String
            get() = when(reportType) {
                ReportType.POST -> "Please provide more details about the post"
                ReportType.USER -> "Please provide more details about the account"
                ReportType.POST_TRANSACTION -> "Describe any issues that occurred during your transaction with $username"
            }

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
        val navArgs = savedStateHandle.toRoute<ReportScreen.Details>()

        applyMutation {
            copy(
                reportType = navArgs.reportType,
                reason = navArgs.reason,
                postId = navArgs.postId,
                userId = navArgs.userId
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

    fun onSubmitPressed() {
        viewModelScope.launch {
            applyMutation {
                copy(
                    loadingSubmit = true
                )
            }

            try {
                // TODO Report message too?
                when(stateValue().reportType) {
                    ReportType.POST -> settingsRepository.reportPost(
                        id = stateValue().postId,
                        uid = stateValue().userId,
                        reason = stateValue().reason
                    )
                    ReportType.USER -> settingsRepository.reportProfile(
                        uid = stateValue().userId,
                        reason = stateValue().reason,
                        description = stateValue().typedContent
                    )
                    ReportType.POST_TRANSACTION -> //TODO  REPLACE THIS LATER WHEN BACKEND REPLIES THIS WILL BREAK
                        feedbackRepository.sendFeedback(
                            uid = stateValue().userId,
                            content = stateValue().typedContent
                        )
                }

                val navArgs = savedStateHandle.toRoute<ReportScreen.Details>()
                reportNavigationRepository.navigate(
                    ReportScreen.Confirmation(
                        reportType = navArgs.reportType,
                        userId = navArgs.userId
                    )
                )
            } catch (e: Exception) {
                Log.e("ReportDetailsViewModel", "Error submitting report", e)
                rootConfirmationRepository.showError(
                    message = "Error submitting report. Please try again later."
                )
                applyMutation {
                    copy(
                        loadingSubmit = false
                    )
                }
            }
        }
    }
}
