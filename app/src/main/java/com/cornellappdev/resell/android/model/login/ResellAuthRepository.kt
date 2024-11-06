package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.CreateUserBody
import com.cornellappdev.resell.android.model.api.LoginBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellAuthRepository @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val retrofitInstance: RetrofitInstance,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
) {

    suspend fun getGoogleUser(id: String): User {
        val users = retrofitInstance.loginApi.getGoogleUser(id)

        return users.user
    }

    // TODO may be redundant
    suspend fun loginToResell(idToken: String, user: String) =
        retrofitInstance.loginApi.login(
            LoginBody(
                idToken = idToken,
                user = user,
                deviceToken = firebaseMessagingRepository.getDeviceFCMToken()!!
            )
        )

    suspend fun createUser(createUserBody: CreateUserBody) =
        retrofitInstance.loginApi.createUser(createUserBody)

    /**
     * Hits the Resell backend to check if our auth session is still valid. If not,
     * it will attempt to re-authenticate. Requires that the user's Id has been set in
     * `userInfoRepository`.
     *
     * At the end, with a successful auth session, it will store the new access token, and
     * proceed, such that the access token can be accessed by `userInfoRepository`.
     *
     * If for any reason the auth fails, an exception will be thrown.
     */
    suspend fun authenticate() {
        // Allowed by precondition
        val userId = userInfoRepository.getUserId()!!

        val response = retrofitInstance.loginApi.getSession(userId)
        var session = response.sessions[0]

        if (!session.active) {
            Log.d("ResellAuthRepository", "Session is not active. Refreshing...")
            session = retrofitInstance.loginApi.refreshSession(session.refreshToken).session
        }

        Log.d(
            "ResellAuthRepository",
            "Session is active with access token: ${session.accessToken} " +
                    "and refresh token: ${session.refreshToken}"
        )
        userInfoRepository.storeAccessToken(session.accessToken)
    }
}
