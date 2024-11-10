package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitInstance @Inject constructor() {
    private var accessToken: String? = null

    /**
     * Updates the retrofit instance's access token.
     */
    fun updateAccessToken(token: String) {
        accessToken = token
    }

    private val authInterceptor = Interceptor { chain ->
        val token = accessToken
        val requestBuilder = chain.request().newBuilder()

        // Add the authorization header only if the token is available
        if (token != null) {
            requestBuilder.addHeader("Content-Type", "application/json")
            if (chain.request().headers["Authorization"] == null) {
                requestBuilder.addHeader("Authorization", "$token")
            }
            Log.d(
                "RetrofitInstance",
                "Access token found: $token for url: ${chain.request().url}"
            )
        } else {
            Log.e(
                "RetrofitInstance",
                "No access token found for url: ${chain.request().url}."
            )
        }

        chain.proceed(requestBuilder.build())
    }

    // Build OkHttpClient with the dynamic auth interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val coreApi: CoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoreApiService::class.java)
    }

    val loginApi: LoginApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApiService::class.java)
    }

    val postsApi: PostsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostsApiService::class.java)
    }

    val requestsApi: RequestsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RequestsApiService::class.java)
    }

    val userApi: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }

    val settingsApi: SettingsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SettingsApiService::class.java)
    }
}
