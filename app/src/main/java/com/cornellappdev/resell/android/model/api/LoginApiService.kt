package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.UserInfo
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("auth")
    suspend fun authorize(@Body authorizeBody: AuthorizeBody): User?
}

data class UserResponse(
    val user: User
)

@Serializable
data class User(
    @SerializedName("firebaseUid") val id: String,
    val username: String,
    val netid: String,
    val givenName: String,
    val familyName: String,
    val photoUrl: String,
    val venmoHandle: String?,
    val bio: String,
    val admin: Boolean,
    val email: String,
    val googleId: String,
    val isActive: Boolean
) {
    fun toUserInfo() = UserInfo(
        username = username,
        name = "$givenName $familyName",
        imageUrl = photoUrl,
        netId = netid,
        venmoHandle = venmoHandle ?: "",
        bio = bio,
        id = id,
        email = email,
    )
}

data class UsersResponse(
    val users: List<User>
)

data class AuthorizeBody(
    val token: String,
)
