package com.cornellappdev.resell.android.model.ptf

import com.cornellappdev.resell.android.model.api.CreateTransactionReviewRequest
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.Transaction
import com.cornellappdev.resell.android.model.api.TransactionReview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostTransactionRatingRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getTransactionByPostId(postId: String): Transaction {
        return retrofitInstance.transactionApi.getTransactionByPostId(postId).transaction
    }

    suspend fun getTransactionById(id: String): Transaction {
        return retrofitInstance.transactionApi.getTransactionById(id).transaction
    }

    suspend fun completeTransaction(transactionId: String): Transaction {
        return retrofitInstance.transactionApi.completeTransaction(transactionId).transaction
    }

    // TODO(backend): replace with a direct by-id lookup once the backend route is fixed.
    // See the TODO on TransactionReviewApiService.getTransactionReviewsBySeller.
    suspend fun hasExistingReview(transactionId: String, sellerId: String): Boolean {
        val response = retrofitInstance.transactionReviewApi.getTransactionReviewsBySeller(sellerId)
        return response.reviews.any { it.transaction?.id == transactionId }
    }

    suspend fun submitTransactionReview(
        transactionId: String,
        stars: Int,
        reviewText: String?
    ): TransactionReview {
        return retrofitInstance.transactionReviewApi.createTransactionReview(
            CreateTransactionReviewRequest(
                transactionId = transactionId,
                stars = stars,
                comments = reviewText
            )
        )
    }
}