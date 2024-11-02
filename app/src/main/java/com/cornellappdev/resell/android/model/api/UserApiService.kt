package com.cornellappdev.resell.android.model.api

import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {

    @GET("user/id/{id}")
    suspend fun getUser(@Path("id") id: String): UserResponse
}
