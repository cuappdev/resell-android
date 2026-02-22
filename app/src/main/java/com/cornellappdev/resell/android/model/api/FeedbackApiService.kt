package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.POST
interface FeedbackApiService {
    @POST("feedback")
    suspend fun createFeedback(@Body reportBody: FeedbackBody): FeedbackBody

    @POST("feedback/search")
    suspend fun searchFeedback(@Body reportBody : SearchFeedback): SearchFeedback
}

data class FeedbackBody(
    val description: String,
    val images: List<String> = emptyList(),
    val userId: String,
)

data class SearchFeedback(
    val query: String
)
