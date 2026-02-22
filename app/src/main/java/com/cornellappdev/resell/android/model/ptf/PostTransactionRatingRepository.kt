package com.cornellappdev.resell.android.model.ptf

import com.cornellappdev.resell.android.model.api.CreateTransactionRequest
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
    suspend fun getTransactionDetails(transactionId: String): Transaction {
        return retrofitInstance.transactionApi.getTransactionById(transactionId).transaction
    }

    suspend fun getTransactionByPostId(postId: String): Transaction {
        return retrofitInstance.transactionApi.getTransactionByPostId(postId).transaction
    }

    suspend fun getTransactionById(id: String): Transaction {
        return retrofitInstance.transactionApi.getTransactionById(id).transaction
    }

    suspend fun getTransactionReviewByTransactionId(transactionId: String): TransactionReview {
        return retrofitInstance.transactionReviewApi.getTransactionReviewByTransactionId(transactionId)
    }

    suspend fun createTransaction(
        postId: String,
        buyerId: String,
        sellerId: String,
        location: String,
        amount: Float,
        transactionDate: String?
    ): Transaction {
        return retrofitInstance.transactionApi.createTransaction(
            CreateTransactionRequest(
                postId = postId,
                buyerId = buyerId,
                sellerId = sellerId,
                location = location,
                amount = amount,
                transactionDate = transactionDate
            )
        ).transaction
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