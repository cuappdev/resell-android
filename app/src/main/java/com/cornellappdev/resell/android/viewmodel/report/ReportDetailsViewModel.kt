package com.cornellappdev.resell.android.viewmodel.report

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportDetailsViewModel @Inject constructor(
    private val reportNavigationRepository: ReportNavigationRepository,
) :
    ResellViewModel<ReportDetailsViewModel.ReportDetailsUiState>(
        initialUiState = ReportDetailsUiState(
            reportPost = true,
            reason = "",
            typedContent = ""
        )
    ) {


    data class ReportDetailsUiState(
        private val reportPost: Boolean,
        val loadingSubmit: Boolean = false,
        val reason: String,
        val typedContent: String
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
        // TODO: Extract report type and other data from nav args.
        applyMutation {
            copy(
                reportPost = false,
                reason = ""
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
        // TODO: Send report
        viewModelScope.launch {
            applyMutation {
                copy(
                    loadingSubmit = true
                )
            }

            delay(1000)

            reportNavigationRepository.navigate(ReportScreen.Confirmation)
        }
    }
}
