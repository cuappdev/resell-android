package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.RequestListing
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.util.richieUserInfo
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RequestsApiService {
    @GET("request/userId/{id}")
    suspend fun getRequestsByUser(@Path("id") id: String): RequestsResponse

    @DELETE("request/id/{id}")
    suspend fun deleteRequest(@Path("id") id: String): RequestResponse

    @POST("request")
    suspend fun createRequest(@Body request: PostRequestBody): RequestResponse

    @GET("request/id/{id}")
    suspend fun getRequest(@Path("id") id: String): RequestResponse
}

data class PostRequestBody(
    val title: String,
    val description: String,
    val userId: String
)

data class RequestsResponse(
    val requests: List<Request>
)

data class RequestResponse(
    val request: Request
)

data class Request(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("user") private val userNullable: User?,
    @SerializedName("matches") private val matchesNullable: List<Post>?
) {
    val matches
        get() = matchesNullable ?: listOf()

    fun toRequestListing(): RequestListing {
        return RequestListing(
            id = id,
            title = title,
            description = description,
            user = userNullable?.toUserInfo() ?: richieUserInfo, // TODO lmao this is so sus
            matches = matches.map { it.toListing() }
        )
    }
}
