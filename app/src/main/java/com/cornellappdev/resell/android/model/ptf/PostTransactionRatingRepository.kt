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