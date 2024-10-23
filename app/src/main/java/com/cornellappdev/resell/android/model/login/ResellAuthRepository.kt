package com.cornellappdev.resell.android.model.login

import com.cornellappdev.resell.android.model.api.GoogleUser
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellAuthRepository @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val retrofitInstance: RetrofitInstance,
) {

    suspend fun getGoogleUser(id: String): GoogleUser {
        val users = retrofitInstance.loginApi.getGoogleUser(id)

        if (users.isEmpty()) {
            throw Exception("No Google User found")
        }

        return users[0]
    }

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

        val session = retrofitInstance.loginApi.getSession(userId)
    }
}
