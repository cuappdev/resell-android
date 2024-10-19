package com.cornellappdev.resell.android.viewmodel.report

import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportReasonViewModel @Inject constructor(
    private val reportNavigationRepository: ReportNavigationRepository
) :
    ResellViewModel<ReportReasonViewModel.ReportReasonUiState>(
        initialUiState = ReportReasonUiState(
            reportPost = true
        )
    ) {

    /**
     * @param reportPost True if it's a report of a post, false if it's a report of a user.
     */
    data class ReportReasonUiState(
        private val reportPost: Boolean
    ) {
        val reasons: List<String>
            get() = ReportReason.entries.filter {
                if (reportPost) {
                    it.appliesToPost
                } else {
                    it.appliesToUser
                }
            }.map { it.message }

        val title: String
            get() = if (reportPost) {
                "Report Post"
            } else {
                "Report Account"
            }

        val subtitle: String
            get() = if (reportPost) {
                "Why do you want to report this post?"
            } else {
                "Why do you want to report this account?"
            }
    }

    init {
        // TODO: Extract report type and other data from nav args.
        applyMutation {
            copy(
                reportPost = false
            )
        }
    }

    fun onReasonPressed(reason: String) {
        // TODO add reason to nav args
        reportNavigationRepository.navigate(ReportScreen.Details)
    }
}

enum class ReportReason(
    val appliesToPost: Boolean = true,
    val appliesToUser: Boolean = true,
    val message: String
) {
    FRAUD(
        appliesToPost = false,
        message = "Fraudulent behavior"
    ),
    ILLEGAL(
        message = "Sale of Illegal items"
    ),
    HATE_SPEECH(
        message = "Hate speech or symbols"
    ),
    BULLYING(
        message = "Bullying or harassment"
    ),
    SEXUAL_MISCONDUCT(
        message = "Sexual misconduct or nudity"
    ),
    SPAM(
        appliesToUser = false,
        message = "Spam"
    ),
    INTELLECTUAL_PROPERTY(
        appliesToPost = false,
        message = "Unauthorized use of intellectual property"
    ),
    OTHER(
        message = "Other"
    )
}
