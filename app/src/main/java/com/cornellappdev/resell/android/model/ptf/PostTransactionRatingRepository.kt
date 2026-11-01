package com.cornellappdev.resell.android.model.ptf

import com.cornellappdev.resell.android.model.api.CreateTransactionReviewRequest
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.Transaction
import com.cornellappdev.resell.android.model.api.TransactionReview
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