package com.cornellappdev.resell.android.model.core

import com.cornellappdev.resell.android.model.classes.GoogleUser
import retrofit2.http.GET
import retrofit2.http.Path

interface CoreApiService {
    @GET("users/googleId/{id}")
    suspend fun getUserById(@Path("id") id: String): GoogleUser
}
