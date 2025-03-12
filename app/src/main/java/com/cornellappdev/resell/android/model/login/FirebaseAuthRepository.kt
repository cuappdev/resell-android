package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userInfoRepository: UserInfoRepository
) {

    /**
     * Performs a purely firebase-SDK sign in.
     */
    suspend fun firebaseSignIn(idToken: String): AuthResult {
        // Create a credential using the Google ID Token
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return try {
            // Sign in with the Firebase auth using the credential
            firebaseAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepository", "Error signing in with Google")
            throw e
        }
    }

    /**
     * Retrieves the FIREBASE access token (as opposed to the Google OAuth token).
     *
     * Requires that [firebaseSignIn] has executed successfully.
     *
     * If retrieved successfully, will also store the access token in [UserInfoRepository]
     * for use in the retrofit interceptor.
     */
    suspend fun getFirebaseAccessToken(): String? {
        val token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        if (token == null) {
            Log.e("FirebaseAuthRepository", "Access token is null.")
            return null
        }

        userInfoRepository.storeAccessToken(token)
        return token
    }

}
