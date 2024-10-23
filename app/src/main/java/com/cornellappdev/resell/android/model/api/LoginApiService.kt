package com.cornellappdev.resell.android.model.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date

interface LoginApiService {
    @GET("users/googleId/{id}")
    suspend fun getGoogleUser(@Path("id") id: String): Array<GoogleUser>

    @POST("auth/login")
    suspend fun login(email: String, password: String): GoogleUser

    @POST("auth/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): AuthResponse
}

data class GoogleUser(
    val email: String,
    val name: String,
    val id: String
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("expiresAt") val expiresAt: Date,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("deviceToken") val deviceToken: String,
    @SerializedName("user") val user: GoogleUser
)
