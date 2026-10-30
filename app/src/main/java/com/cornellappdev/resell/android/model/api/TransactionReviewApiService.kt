package com.cornellappdev.resell.android.model.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TransactionReviewApiService {
    @POST("transactionReview/")
    suspend fun createTransactionReview(@Body request: CreateTransactionReviewRequest): TransactionReview

    // TODO(backend): the "correct" lookup, GET transactionReview/transactionId/{id}/, always
    // 404, but once it works, delete this, PostTransactionRatingRepository.hasExistingReview's
    // sellerId param, and the 3 types below, and call the fixed endpoint
    // directly by transaction id instead.
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
    val id: String? = null,
    val buyer: TransactionReviewReviewer? = null
)

data class TransactionReviewReviewer(
    @SerializedName("firebaseUid") val id: String? = null,
    val username: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val photoUrl: String? = null
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