package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.POST
interface FeedbackApiService {
    @POST("feedback")
    suspend fun createFeedback(@Body reportBody: FeedbackBody): FeedbackBody
}

data class FeedbackBody(
    val description: String,
    val images: List<String> = emptyList(),
    val userId: String,
)
