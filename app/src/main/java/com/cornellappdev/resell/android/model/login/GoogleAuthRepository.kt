package com.cornellappdev.resell.android.model.login

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.cornellappdev.resell.android.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The Google account info needed to create a Resell user.
 *
 * @param googleId The numeric Google account id (the ID token's `sub`), NOT the email.
 */
data class GoogleProfile(
    val email: String,
    val givenName: String,
    val familyName: String,
    val photoUrl: String,
    val googleId: String,
)

@Singleton
class GoogleAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext context: Context,
) {

    companion object {
        private const val TAG = "GoogleAuthRepository"
        private const val GOOGLE_PROVIDER_ID = "google.com"
        private const val CORNELL_DOMAIN = "cornell.edu"
    }

    private val credentialManager = CredentialManager.create(context)

    /**
     * Profile from the most recent interactive sign in. Lost on process death, in which case
     * [profileOrNull] falls back to the Firebase user.
     */
    private var cachedProfile: GoogleProfile? = null

    /**
     * Shows the Sign in with Google flow, then signs in to Firebase with the resulting ID token.
     *
     * @param activityContext Must be an Activity context; Credential Manager launches UI from it.
     * @throws androidx.credentials.exceptions.GetCredentialCancellationException if the user
     *  dismissed the account chooser.
     * @throws androidx.credentials.exceptions.GetCredentialException for other sign in failures.
     */
    suspend fun signIn(activityContext: Context): GoogleProfile {
        val option = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .setHostedDomainFilter(CORNELL_DOMAIN)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val credential = credentialManager.getCredential(activityContext, request).credential
        check(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Unexpected credential type: ${credential.type}" }
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)

        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val user = authResult.user!!
        Log.d(TAG, "Success: ${user.uid}")

        val googleId = user.providerData.first { it.providerId == GOOGLE_PROVIDER_ID }.uid
        return GoogleProfile(
            email = checkNotNull(googleCredential.email ?: user.email) { "Google account has no email" },
            givenName = googleCredential.givenName.orEmpty(),
            familyName = googleCredential.familyName.orEmpty(),
            photoUrl = googleCredential.profilePictureUri?.toString().orEmpty(),
            googleId = googleId,
        ).also { cachedProfile = it }
    }

    /**
     * Returns the email of the signed in user, or null if not signed in.
     *
     * Firebase persists its session across launches, so this is the auto-login check.
     */
    fun signedInEmailOrNull(): String? = firebaseAuth.currentUser?.email

    /**
     * Returns the signed in user's Google profile, or null if not signed in.
     */
    fun profileOrNull(): GoogleProfile? {
        cachedProfile?.let { return it }

        val googleInfo = firebaseAuth.currentUser?.providerData
            ?.firstOrNull { it.providerId == GOOGLE_PROVIDER_ID }
            ?: return null
        val names = googleInfo.displayName.orEmpty().trim().split(" ", limit = 2)
        return GoogleProfile(
            email = googleInfo.email ?: return null,
            givenName = names.getOrElse(0) { "" },
            familyName = names.getOrElse(1) { "" },
            photoUrl = googleInfo.photoUrl?.toString().orEmpty(),
            googleId = googleInfo.uid,
        )
    }

    /**
     * Sign out. Call if the user logs in with a non-cornell email, or whenever a log-out should occur.
     *
     * After calling this, the user will no longer auto-navigate to home, and google auth will
     * correctly query for a new email.
     */
    suspend fun signOut() {
        cachedProfile = null
        firebaseAuth.signOut()
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Failed to clear credential state", e)
        }
    }
}
