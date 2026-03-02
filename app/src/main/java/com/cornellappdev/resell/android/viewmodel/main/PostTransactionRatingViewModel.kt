package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.ptf.PostTransactionRatingRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.submitted.ConfettiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PostTransactionRatingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val confettiRepository: ConfettiRepository,
    private val postTransactionRatingRepository: PostTransactionRatingRepository
) : ResellViewModel<PostTransactionRatingViewModel.PostTransactionRatingUiState>(
    initialUiState = PostTransactionRatingUiState()
) {

    data class PostTransactionRatingUiState(
        val imageUrl: String = "",
        val itemName: String = "",
        val price: String = "",
        val sellerName: String = "",
        val date: Date = Date(),
        val rating: Int = 0,
        val reviewText: String = "",
        val postId: String = "",
        val userId: String = "",
        val transactionId: String = ""
    )

    init {
        // Fetch arguments from navigation
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.POST_TRANSACTION_RATING>()
        applyMutation {
            copy(
                postId = navArgs.postId,
                userId = navArgs.userId
            )
        }

        viewModelScope.launch {
            try {
                val transactionFromPostId = postTransactionRatingRepository.getTransactionByPostId(navArgs.postId)
                val transaction = postTransactionRatingRepository.getTransactionById(transactionFromPostId.id)

                val date = transaction.transactionDate

                val sellerFullName = transaction.seller?.let { seller ->
                    listOfNotNull(seller.givenName, seller.familyName)
                        .joinToString(" ")
                        .ifBlank{"Unknown Seller"}
                } ?: "Unknown Seller"

                applyMutation {
                    copy(
                        transactionId = transaction.id,
                        itemName = transaction.post.title,
                        price = String.format("%.2f", transaction.amount),
                        sellerName = sellerFullName,
                        date = date,
                        imageUrl = transaction.post.images.firstOrNull() ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e("PostTransactionRatingViewModel", "Error loading transaction", e)
            }
        }
    }

    fun onFeedbackClicked() {
        rootNavigationRepository.navigate(
            ResellRootRoute.FEEDBACK(
                postId = uiStateFlow.value.postId,
                userId = uiStateFlow.value.userId,
                userName = uiStateFlow.value.sellerName,
            )
        )
    }

    fun onBackArrow() {
        rootNavigationRepository.popBackStack()
    }

    fun onRatingChanged(newRating: Int) {
        applyMutation { copy(rating = newRating) }
    }

    fun onReviewTextChanged(newText: String) {
        applyMutation { copy(reviewText = newText) }
    }

    fun submitReview() {
        viewModelScope.launch {
            try {
                val currentUiState = uiStateFlow.value

                postTransactionRatingRepository.submitTransactionReview(
                    transactionId = currentUiState.transactionId,
                    stars = currentUiState.rating,
                    reviewText = currentUiState.reviewText
                )

                // Navigate back home
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
            } catch (e: Exception) {
                Log.e("PostTransactionRatingViewModel", "Error submitting review.", e)
                // Error code = 500 for all exceptions (including duplicate transaction reviews)
                rootConfirmationRepository.showError(
                    "Failed to submit review. A review may already exist, or please try again later."
                )
            }
        }
    }
}