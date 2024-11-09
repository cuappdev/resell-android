package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.UserInfo
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApiService {
    @GET("user/googleId/{id}")
    suspend fun getGoogleUser(@Path("id") id: String): UserResponse

    @POST("auth/login")
    suspend fun login(
        @Body loginBody: LoginBody
    ): UserSession

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse

    @GET("auth/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): SessionResponse<UserSession>

    @GET("auth/refresh")
    suspend fun refreshSession(
        @Header("Authorization") refreshToken: String
    ): SingleSessionResponse

    @POST("auth")
    suspend fun createUser(@Body createUserBody: CreateUserBody): UserResponse
}

data class LoginBody(
    @SerializedName("idToken") val idToken: String,
    @SerializedName("user") val user: String,
    @SerializedName("deviceToken") val deviceToken: String
)

data class UserSession(
    @SerializedName("userId") val userId: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("expiresAt") val expiresAt: Long,
    @SerializedName("refreshToken") val refreshToken: String
)

data class SessionResponse<T>(
    @SerializedName("sessions") val sessions: List<T>
)

data class LogoutResponse(
    @SerializedName("logoutSuccess") val logoutSuccess: Boolean
)

data class SingleSessionResponse(
    @SerializedName("session") val session: UserSession
)

data class CreateUserBody(
    val username: String,
    val netid: String,
    val givenName: String,
    val familyName: String,
    val photoUrl: String,
    val email: String,
    val googleId: String,
    val bio: String
)

data class UserResponse(
    val user: User
)

@Serializable
data class User(
    val id: String,
    val username: String,
    val netid: String,
    val givenName: String,
    val familyName: String,
    val admin: Boolean,
    val photoUrl: String,
    val bio: String,
    val email: String,
    val googleId: String
) {
    fun toUserInfo() = UserInfo(
        username = username,
        name = "$givenName $familyName",
        imageUrl = photoUrl,
        netId = netid,
        // TODO Refactor UserInfo because we don't have all these fields
        venmoHandle = "TODO",
        bio = bio,
        id = id
    )
}

data class UsersResponse(
    val users: List<User>
)

data class GoogleUser(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val admin: Boolean,
    val profilePictureUrl: String,
    val venmoHandle: String,
    val email: String,
    val googleId: String,
    val bio: String
)
