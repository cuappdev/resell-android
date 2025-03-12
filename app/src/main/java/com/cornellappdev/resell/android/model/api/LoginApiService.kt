package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.UserInfo
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("authorize")
    suspend fun authorize(@Body authorizeBody: AuthorizeBody): UserResponse?
}

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
        id = id,
        email = email
    )
}

data class UsersResponse(
    val users: List<User>
)

data class AuthorizeBody(
    val fcmToken: String,
)
