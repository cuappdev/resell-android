package com.cornellappdev.resell.android.viewmodel.report

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
    private val rootConfirmationRepository: RootConfirmationRepository
) :
    ResellViewModel<ReportDetailsViewModel.ReportDetailsUiState>(
        initialUiState = ReportDetailsUiState(
            reportPost = true,
            reason = "",
            typedContent = ""
        )
    ) {


    data class ReportDetailsUiState(
        val reportPost: Boolean,
        val loadingSubmit: Boolean = false,
        val reason: String,
        val typedContent: String,
        val postId: String = "",
        val userId: String = "",
    ) {
        val title: String
            get() = if (reportPost) {
                "Report Post"
            } else {
                "Report Account"
            }

        val body: String
            get() = if (reportPost) {
                "Please provide more details about the post"
            } else {
                "Please provide more details about the account"
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
                reportPost = navArgs.reportPost,
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
                if (stateValue().reportPost) {
                    settingsRepository.reportPost(
                        id = stateValue().postId,
                        uid = stateValue().userId,
                        reason = stateValue().reason
                    )
                } else {
                    settingsRepository.reportProfile(
                        uid = stateValue().userId,
                        reason = stateValue().reason,
                        description = stateValue().typedContent
                    )
                }

                val navArgs = savedStateHandle.toRoute<ReportScreen.Details>()
                reportNavigationRepository.navigate(
                    ReportScreen.Confirmation(
                        reportPost = navArgs.reportPost,
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
