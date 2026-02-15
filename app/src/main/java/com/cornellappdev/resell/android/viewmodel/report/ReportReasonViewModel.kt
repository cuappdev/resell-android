package com.cornellappdev.resell.android.viewmodel.report

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.Report
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
            reportType = ReportType.POST
        )
    ) {

    /**
     * @param reportPost True if it's a report of a post, false if it's a report of a user.
     */
    data class ReportReasonUiState(
        private val reportType: ReportType,
    ) {
        val user_reasons : List<ReportReason> = listOf(ReportReason.FRAUD, ReportReason.ILLEGAL, ReportReason.HATE_SPEECH, ReportReason.BULLYING, ReportReason.SEXUAL_MISCONDUCT, ReportReason.INTELLECTUAL_PROPERTY, ReportReason.OTHER)
        val post_reasons : List<ReportReason> = listOf(ReportReason.ILLEGAL, ReportReason.HATE_SPEECH, ReportReason.BULLYING, ReportReason.SEXUAL_MISCONDUCT, ReportReason.SPAM, ReportReason.OTHER)
        val ptf_reasons : List<ReportReason> = listOf(ReportReason.STOPPED_RESPONDING, ReportReason.ITEM_DAMAGED, ReportReason.ITEM_DIFFERENT, ReportReason.APP_ERROR, ReportReason.OTHER)


        val reasons: List<String>
            get() =
                if(reportType == ReportType.POST_TRANSACTION) {
                    ptf_reasons.map { it.message }
                }
                else if (reportType == ReportType.POST) {
                    post_reasons.map { it.message }
                }
                else {
                    user_reasons.map { it.message }
                }


        val title: String
            get() = if(reportType == ReportType.POST_TRANSACTION) {
                "Submit Feedback"
            }
            else if (reportType == ReportType.POST) {
                "Report Post"
            }
            else {
                "Report Account"
            }

        val subtitle: String
            get() = if(reportType == ReportType.POST_TRANSACTION) {
                "Explain what happened:"
            }
            else if (reportType == ReportType.POST) {
                "Why do you want to report this post?"
            }
            else {
                "Why do you want to report this account?"
            }
    }

    init {
        // TODO: User Id stuff
        savedStateHandle.toRoute<ReportScreen.Reason>().let { navArgs ->
            applyMutation {
                copy(
                    reportType = ReportType.POST
                )
            }
        }
    }

    fun onReasonPressed(reason: String) {
        // TODO add reason to nav args
        val navArgs = savedStateHandle.toRoute<ReportScreen.Reason>()
        reportNavigationRepository.navigate(
            ReportScreen.Details(
                reportType = navArgs.reportType,
                postId = navArgs.postId,
                userId = navArgs.userId,
                reason = reason
            )
        )
    }
}

// TODO: make hardcoded list of report reasons for each type (user/post/ptf)
// TODO: use enum for the text stuff later in ReportDetailsViewModel
// TODO: delete all the bools
enum class ReportReason(
    val message: String
) {
    STOPPED_RESPONDING(
        message = "User stopped responding"
    ),
    ITEM_DAMAGED(
        message = "Item arrived damaged"
    ),
    ITEM_DIFFERENT(
        message = "Item is different than what I expected"
    ),
    APP_ERROR(
        message = "There was an error on the app"
    ),
    FRAUD(
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
        message = "Spam"
    ),
    INTELLECTUAL_PROPERTY(
        message = "Unauthorized use of intellectual property"
    ),
    OTHER(
        message = "Other"
    )
}

//TODO : add enum params
enum class ReportType {
    POST,
    USER,
    POST_TRANSACTION
}
