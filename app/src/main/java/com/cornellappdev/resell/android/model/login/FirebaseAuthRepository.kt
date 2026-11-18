package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userInfoRepository: UserInfoRepository
) {

    /**
     * Retrieves the FIREBASE access token (as opposed to the Google OAuth token).
     *
     * Requires that the user is signed in to Firebase (see [GoogleAuthRepository.signIn]).
     *
     * If retrieved successfully, will also store the access token in [UserInfoRepository]
     * for use in the retrofit interceptor.
     *
     * @param forceRefresh If true, fetches a new token from the server even if the cached one is
     *  still valid. Throws if the user's Firebase session has been revoked.
     */
    suspend fun getFirebaseAccessToken(forceRefresh: Boolean = false): String? {
        val token = firebaseAuth.currentUser?.getIdToken(forceRefresh)?.await()?.token
        if (token == null) {
            Log.e("FirebaseAuthRepository", "Access token is null.")
            return null
        }

        userInfoRepository.storeAccessToken(token)
        return token
    }

}
