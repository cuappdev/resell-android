package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.AuthorizeBody
import com.cornellappdev.resell.android.model.api.CreateUserBody
import com.cornellappdev.resell.android.model.api.LoginBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.model.api.UserResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResellAuthRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
) {
    suspend fun createUser(createUserBody: CreateUserBody) =
        retrofitInstance.loginApi.createUser(createUserBody)

    /**
     * Hits the Resell backend's (POST /authorize) to perform an initial authorization.
     *
     * Returns the user, if it exists. If empty, the caller should direct the user to
     * create a new account.
     */
    suspend fun authenticate(): User? {
        val fcmToken = firebaseMessagingRepository.getDeviceFCMToken()

        val response = retrofitInstance.loginApi.authorize(
            AuthorizeBody(
                fcmToken = fcmToken
            )
        )

        return response?.user
    }
}
