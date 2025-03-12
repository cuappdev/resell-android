package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @GET("user/id/{id}")
    suspend fun getUser(@Path("id") id: String): UserResponse

    @POST("user/block")
    suspend fun blockUser(@Body body: BlockBody): UserResponse

    @POST("user/unblock")
    suspend fun unblockUser(@Body body: UnblockBody): UserResponse

    @GET("user/blocked/id/{id}")
    suspend fun getBlockedUsers(@Path("id") id: String): UsersResponse

    @POST("user/softDelete/id/{id}")
    suspend fun softDeleteUser(@Path("id") id: String): UserResponse

    @POST("image")
    suspend fun uploadImage(@Body body: ImageBody): ImageResponse

    @POST("user/create")
    suspend fun createUser(@Body createUserBody: CreateUserBody): UserResponse
}

data class CreateUserBody(
    val fcmToken: String,
    val username: String,
    val netid: String,
    val givenName: String,
    val familyName: String,
    val photoUrl: String,
    val email: String,
    val googleId: String,
    val bio: String
)

data class ImageBody(
    val imageBase64: String
)

data class ImageResponse(
    /** URL */
    val image: String
)

data class BlockBody(
    val blocked: String
)

data class UnblockBody(
    val unblocked: String
)
