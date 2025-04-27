package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.BuildConfig
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FirebaseAuthRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitInstance @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userInfoRepository: UserInfoRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
) {
    private var cachedToken: String? = null

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs JSON responses
    }

    /**
     * Provides the firebase access token to the interceptor.
     */
    private val authInterceptor = Interceptor { chain ->
        // If the token is cached, use it. Otherwise, fetch it.
        val token = cachedToken ?: runBlocking {
            cachedToken = userInfoRepository.getAccessToken()
            cachedToken
        }

        val requestBuilder = chain.request().newBuilder()

        // Add the authorization header only if the token is available
        if (token != null) {
            requestBuilder.addHeader("Content-Type", "application/json")
            if (chain.request().headers["Authorization"] == null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
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

        val response = chain.proceed(requestBuilder.build())
        val responseBody = response.body?.string() // Read the response body

        if (!response.isSuccessful) {
            // Log the error response body for debugging
            Log.e("OkHttpErrorResponse", "Error response body: $responseBody")

            // Get the `errors` response
            try {
                val jsonObject = JSONObject(responseBody ?: "")
                val errors = jsonObject.optJSONArray("errors")
                if (errors != null) {
                    Log.e("OkHttpErrorResponse", "Errors: $errors")
                }
            } catch (e: Exception) {
                Log.e("OkHttpErrorResponse", "Error parsing the error response", e)
            }
        }

        response.newBuilder()
            .body((responseBody ?: "").toResponseBody(response.body?.contentType()))
            .build()
    }

    private val authenticator = Authenticator { _, response ->
        if (responseCount(response) >= 2) {
            // Already retried once, still getting 401 — force sign out
            runBlocking {
                googleAuthRepository.signOut()
                rootNavigationRepository.navigate(ResellRootRoute.LANDING)
                rootConfirmationRepository.showError(
                    message = "Authentication Failed. Please try signing in again!"
                )
            }
            return@Authenticator null // Give up — don't retry again
        }

        // Ping firebase for a refresh. Force refresh.
        val accessToken = runBlocking { firebaseAuthRepository.getFirebaseAccessToken(true) }
        cachedToken = accessToken
        if (accessToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Log.e("RetrofitInstance", "No access token found, even on refresh.")
            null
        }
    }

    /**
     * Helper to count how many times we've already retried this request.
     */
    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    // Build OkHttpClient with the dynamic auth interceptor
    private val okHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(logging)
        }
    }
        .addInterceptor(authInterceptor)
        .authenticator(authenticator)
        .build()

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

    val notificationsApi: FcmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.FCM_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FcmApiService::class.java)
    }

    val chatApi: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}
