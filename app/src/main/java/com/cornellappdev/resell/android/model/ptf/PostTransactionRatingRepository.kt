package com.cornellappdev.resell.android.model.ptf

import android.util.Log
import com.cornellappdev.resell.android.model.api.CreateTransactionReviewRequest
import com.cornellappdev.resell.android.model.api.CreateUserReviewRequest
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.Transaction
import com.cornellappdev.resell.android.model.api.TransactionReview
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
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

    suspend fun hasExistingReview(transactionId: String): Boolean {
        val response =
            retrofitInstance.transactionReviewApi.getTransactionReviewByTransactionId(transactionId)
        return when {
            response.isSuccessful -> true
            response.code() == 404 -> false
            else -> throw HttpException(response)
        }
    }

    /**
     * Submits the transaction review, then mirrors it as a user review since iOS reads a profile's
     * reviews from user reviews.
     */
    suspend fun submitTransactionReview(
        transactionId: String,
        buyerId: String,
        sellerId: String,
        stars: Int,
        reviewText: String?
    ): TransactionReview {
        val review = retrofitInstance.transactionReviewApi.createTransactionReview(
            CreateTransactionReviewRequest(
                transactionId = transactionId,
                stars = stars,
                comments = reviewText
            )
        )

        if (buyerId.isEmpty() || sellerId.isEmpty()) {
            Log.w(TAG, "Skipping user review mirror, transaction $transactionId has no buyer or seller")
            return review
        }

        try {
            retrofitInstance.userReviewApi.createUserReview(
                CreateUserReviewRequest(
                    buyerId = buyerId,
                    sellerId = sellerId,
                    fulfilled = true,
                    stars = stars,
                    comments = reviewText?.ifBlank { null } ?: DEFAULT_USER_REVIEW_COMMENT
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error mirroring user review: ", e)
        }
        return review
    }

    private companion object {
        const val TAG = "PostTransactionRatingRepository"
        const val DEFAULT_USER_REVIEW_COMMENT = "Great transaction!"
    }
}