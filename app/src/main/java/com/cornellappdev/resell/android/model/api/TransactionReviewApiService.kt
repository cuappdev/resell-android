package com.cornellappdev.resell.android.model.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionReviewApiService {
    @POST("transactionReview/")
    suspend fun createTransactionReview(@Body request: CreateTransactionReviewRequest): TransactionReview

    // 200 if the transaction has a review, 404 if it doesn't.
    @GET("transactionReview/transactionId/{id}/")
    suspend fun getTransactionReviewByTransactionId(@Path("id") transactionId: String): Response<ResponseBody>

    @GET("transactionReview/")
    suspend fun getTransactionReviewsBySeller(@Query("sellerId") sellerId: String): TransactionReviewListResponse
}

data class TransactionReviewListResponse(
    val reviews: List<TransactionReviewListItem> = emptyList()
)

data class TransactionReviewListItem(
    val id: String? = null,
    val stars: Int? = null,
    val comments: String? = null,
    val createdAt: String? = null,
    val transaction: TransactionReviewListItemTransaction? = null
)

data class TransactionReviewListItemTransaction(
    val buyer: TransactionReviewReviewer? = null
)

data class TransactionReviewReviewer(
    val username: String? = null,
    val givenName: String? = null
)

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