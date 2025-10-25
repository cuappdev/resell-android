package com.cornellappdev.resell.android.viewmodel.main

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostTransactionRatingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootConfirmationRepository: RootConfirmationRepository
) : ResellViewModel<PostTransactionRatingViewModel.PostTransactionRatingUiState>(
    initialUiState = PostTransactionRatingUiState()
) {

    data class PostTransactionRatingUiState(
        val imageUrl: String = "",
        val itemName: String = "",
        val price: String = "",
        val sellerName: String = "",
        val date: String = "",
        val rating: Int = 0,
        val reviewText: String = "",
        val postId: String = "",
        val userId: String = ""
    )

    init {
        // Fetch arguments from navigation
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.POST_TRANSACTION_RATING>()
        // TODO: Replace with actual data when backend is integrated
        applyMutation {
            copy(
                itemName = "Item Name",
                price = "00.00",
                sellerName = "Seller Name",
                date = "Month, 00, 0000",
                imageUrl = "https://fakelink",
                postId = navArgs.postId,
                userId = navArgs.userId
            )
        }
    }

    // TODO add networking here
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
    }
}