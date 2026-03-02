package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionReviewApiService {
    @POST("transactionReview/")
    suspend fun createTransactionReview(@Body request: CreateTransactionReviewRequest): TransactionReview
}

data class CreateTransactionReviewRequest(
    val transactionId: String,
    val stars: Int,
    val comments: String?
)

data class TransactionReview(
    val id: String,
    val transactionId: String,
    val rating: Int,
    val fulfilled: Boolean,
    val comments: String?,
    val created: String
)