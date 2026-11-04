package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.api.Transaction
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.TransactionInfo
import com.cornellappdev.resell.android.model.chats.TransactionState
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.confirmedMeeting
import com.cornellappdev.resell.android.model.ptf.PostTransactionRatingRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val postTransactionRatingRepository: PostTransactionRatingRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
) : ResellViewModel<ChatTransactionViewModel.TransactionUiState>(
    initialUiState = TransactionUiState(
        isBuyer = savedStateHandle.toRoute<ResellRootRoute.CHAT>().isBuyer
    )
) {
    companion object {
        private const val TAG = "ChatTransactionViewModel"
    }

    data class TransactionUiState(
        val isBuyer: Boolean,
        val activeTransaction: Transaction? = null,
        val completingSaleTransactionId: String? = null,
        val confirmedMeeting: MeetingInfo? = null,
        val scrollBottom: UIEvent<Unit>? = null,
    ) {
        val transactionBannerInfo: TransactionInfo?
            get() = activeTransaction?.let { transaction ->
                if (!transaction.completed) {
                    // If the meeting was cancelled, don't show the transaction prompt.
                    if (confirmedMeeting == null) return@let null
                    // Only show after meeting time has passed
                    if (Date().before(transaction.transactionDate)) return@let null
                }

                TransactionInfo(
                    transactionId = transaction.id,
                    completeTime = Timestamp(transaction.transactionDate),
                    state = if (transaction.completed) {
                        TransactionState.COMPLETED
                    } else {
                        TransactionState.PENDING
                    },
                    actionText = when {
                        !transaction.completed -> "Mark Sale Complete"
                        isBuyer -> "Leave Review"
                        else -> "Sale Completed"
                    },
                    mostRecent = completingSaleTransactionId != transaction.id
                )
            }
    }

    private val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
    private val listing = Json.decodeFromString<Listing>(navArgs.listingJson)

    private var transactionRefreshJob: Job? = null

    fun onTransactionStateClicked(transactionInfo: TransactionInfo) {
        viewModelScope.launch {
            when (transactionInfo.state) {
                TransactionState.PENDING -> {
                    onMarkSaleComplete(transactionInfo.transactionId)
                }
                TransactionState.COMPLETED -> {
                    if (navArgs.isBuyer) {
                        stateValue().activeTransaction?.let { openReviewScreenIfEligible(it) }
                    } else {
                        showSaleCompleteDialog(alreadyComplete = true)
                    }
                }
                TransactionState.CANCELED -> {}
            }
        }
    }

    fun onMarkSaleComplete(transactionId: String) {
        if (stateValue().completingSaleTransactionId != null) return
        val banner = stateValue().transactionBannerInfo
        if (banner?.transactionId != transactionId || banner.state != TransactionState.PENDING) return

        viewModelScope.launch {
            applyMutation { copy(completingSaleTransactionId = transactionId) }
            try {
                val transaction = postTransactionRatingRepository.completeTransaction(transactionId)
                applyMutation {
                    copy(
                        activeTransaction = transaction,
                        completingSaleTransactionId = null
                    )
                }
                if (navArgs.isBuyer) {
                    openReviewScreenIfEligible(transaction)
                } else {
                    showSaleCompleteDialog(alreadyComplete = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error completing transaction: ", e)
                applyMutation { copy(completingSaleTransactionId = null) }
                rootConfirmationRepository.showError(
                    "Couldn't mark the sale complete. Please try again."
                )
            }
        }
    }

    private fun showSaleCompleteDialog(alreadyComplete: Boolean) {
        rootDialogRepository.showDialog(
            RootDialogContent.TwoButtonDialog(
                title = "Sale Complete",
                description = if (alreadyComplete) {
                    "This transaction with ${navArgs.name} has already been marked complete."
                } else {
                    "You've marked this transaction with ${navArgs.name} as complete."
                },
                primaryButtonText = "Dismiss",
                secondaryButtonText = null,
                onPrimaryButtonClick = { rootDialogRepository.dismissDialog() },
                onSecondaryButtonClick = {},
                exitButton = true
            )
        )
    }

    private fun openReviewScreenIfEligible(transaction: Transaction) {
        viewModelScope.launch {
            val alreadyReviewed = try {
                postTransactionRatingRepository.hasExistingReview(transaction.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking for an existing review: ", e)
                false
            }

            if (alreadyReviewed) {
                showAlreadyReviewedDialog()
            } else {
                navigateToReviewScreen()
            }
        }
    }

    private fun showAlreadyReviewedDialog() {
        rootDialogRepository.showDialog(
            RootDialogContent.TwoButtonDialog(
                title = "Already Reviewed",
                description = "You've already left a review for this transaction.",
                primaryButtonText = "Dismiss",
                secondaryButtonText = null,
                onPrimaryButtonClick = { rootDialogRepository.dismissDialog() },
                onSecondaryButtonClick = {},
                exitButton = true
            )
        )
    }

    private fun navigateToReviewScreen() {
        rootNavigationRepository.navigate(
            ResellRootRoute.POST_TRANSACTION_RATING(
                postId = listing.id,
                userId = navArgs.otherUserId
            )
        )
    }

    init {
        asyncCollect(chatRepository.subscribedChatFlow(navArgs.chatId)) { response ->
            applyMutation {
                copy(confirmedMeeting = response.asSuccessOrNull()?.data?.confirmedMeeting())
            }

            if (response is ResellApiResponse.Success) {
                val transactionId = response.data.chatHistory
                    .flatMap { it.messages }
                    .filter { it.transactionId != null }
                    .maxByOrNull { it.timestamp.seconds }
                    ?.transactionId

                if (transactionId != null) {
                    transactionRefreshJob?.cancel()
                    transactionRefreshJob = viewModelScope.launch {
                        try {
                            val transaction = postTransactionRatingRepository.getTransactionById(transactionId)
                            applyMutation {
                                val current = activeTransaction
                                val isStale = current != null && current.id == transaction.id
                                    && current.completed && !transaction.completed
                                copy(
                                    activeTransaction = if (isStale) current else transaction,
                                    scrollBottom = UIEvent(Unit)
                                )
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.e(TAG, "Error checking transaction: ", e)
                            rootConfirmationRepository.showError(
                                "Failed to load transaction. Please come back and try again later."
                            )
                        }
                    }
                }
            }
        }
    }
}
