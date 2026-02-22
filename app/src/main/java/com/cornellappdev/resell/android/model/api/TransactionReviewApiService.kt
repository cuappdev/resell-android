package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionReviewApiService {
    @GET("transactionReview/")
    suspend fun getTransactionReviews(): List<TransactionReview>

    @GET("transactionReview/id/{id}/")
    suspend fun getTransactionReviewById(@Path("id") id: String): TransactionReview

    @GET("transactionReview/transactionId/{transactionId}/")
    suspend fun getTransactionReviewByTransactionId(@Path("transactionId") transactionId: String): TransactionReview

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