package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PostTransactionRatingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootConfirmationRepository: RootConfirmationRepository
    ) : ResellViewModel<PostTransactionRatingViewModel.PostTransactionRatingUiState>(
        initialUiState = PostTransactionRatingUiState(
            submitted = false,
            rating = 0,
            reviewText = ""
        )
    ) {

    data class PostTransactionRatingUiState (
        val submitted : Boolean,
        val rating : Int,
        val reviewText : String
    ) {

    }

    // TODO add networking here
    fun onFeedbackClicked() {
        rootNavigationRepository.navigate(
            ResellRootRoute.FEEDBACK(
                postId = "",
                userId = "",
                userName = "Test User",
            )
        )
    }

    fun onBackArrow() {
        rootNavigationRepository.popBackStack()
    }

    fun onRatingChanged(newRating : Int) {
        applyMutation { copy(rating = newRating) }
    }

    fun onReviewTextChanged(newText : String) {
        applyMutation { copy(reviewText = newText) }
    }

    fun submitReview() {
        viewModelScope.launch {
            try {
                // TODO: API/backend calls

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
            }
            catch (e: Exception) {
                Log.e("PostTransactionRatingViewModel", "Error submitting review: ", e)
                rootConfirmationRepository.showError(
                    "Something went wrong with your submission. Please try again later."
                )
            }
        }
    }
}