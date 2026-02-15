package com.cornellappdev.resell.android.model.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApiService {
    @POST("feedback")
    suspend fun sendFeedback(@Body reportBody: FeedbackBody): FeedbackBody

    @POST("feedback/search")
    suspend fun searchFeedback(@Body reportBody : SearchFeedback): SearchFeedback
}

data class FeedbackBody(
    val userId: String,
    val content: String
)

data class SearchFeedback(
    val query: String
)