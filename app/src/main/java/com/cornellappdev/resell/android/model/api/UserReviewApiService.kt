package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.POST

interface UserReviewApiService {
    @POST("userReview/")
    suspend fun createUserReview(@Body request: CreateUserReviewRequest)
}

data class CreateUserReviewRequest(
    val buyerId: String,
    val sellerId: String,
    val fulfilled: Boolean,
    val stars: Int,
    val comments: String
)
