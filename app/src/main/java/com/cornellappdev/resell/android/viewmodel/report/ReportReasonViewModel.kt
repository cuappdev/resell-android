package com.cornellappdev.resell.android.viewmodel.report

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.reporting.ReportScreen
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.ReportNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportReasonViewModel @Inject constructor(
    private val reportNavigationRepository: ReportNavigationRepository,
    private val savedStateHandle: SavedStateHandle
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
        private val reportPost: Boolean,
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
        // TODO: User Id stuff
        savedStateHandle.toRoute<ReportScreen.Reason>().let { navArgs ->
            applyMutation {
                copy(
                    reportPost = navArgs.reportPost
                )
            }
        }
    }

    fun onReasonPressed(reason: String) {
        // TODO add reason to nav args
        val navArgs = savedStateHandle.toRoute<ReportScreen.Reason>()
        reportNavigationRepository.navigate(
            ReportScreen.Details(
                reportPost = navArgs.reportPost,
                postId = navArgs.postId,
                userId = navArgs.userId,
                reason = reason
            )
        )
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
