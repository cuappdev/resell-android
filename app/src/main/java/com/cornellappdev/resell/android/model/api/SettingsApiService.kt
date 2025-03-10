package com.cornellappdev.resell.android.model.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingsApiService {
    @POST("report/post")
    suspend fun reportPost(@Body reportBody: ReportPostBody): ReportBody

    @POST("report/profile")
    suspend fun reportProfile(@Body reportBody: ReportProfileBody): ReportBody

    @POST("report/message")
    suspend fun reportMessage(@Body reportBody: ReportBody): ReportBody

    @POST("feedback")
    suspend fun sendFeedback(@Body feedback: Feedback)

    @POST("user")
    suspend fun editUser(@Body user: EditUser)
}

data class EditUser(
    val venmoHandle: String,
    val username: String,
    val bio: String,
    @SerializedName("photoUrlBase64") val profilePicBase64: String
)

data class ReportBody(
    val report: Report
)

data class Report(
    val id: String,
    val reporter: Reporter,
//    val reportedContent: ReportedContent,
    val reason: String,
    val type: String,
    val resolved: Boolean,
//    val created: String
)

data class Reporter(
    val id: String,
    val firstName: String,
    val lastName: String,
    val profilePicUrl: String
)

data class ReportPostBody(
    val reported: String,
    val post: String,
    val reason: String
)

data class ReportProfileBody(
    val reported: String,
    val reason: String
)

data class Feedback(
    val description: String,
    @SerializedName("images") val imagesBase64: List<String>,
    val userId: String
)
