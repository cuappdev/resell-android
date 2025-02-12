package com.cornellappdev.resell.android.model.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.cornellappdev.resell.android.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context,
) {

    /**
     * Constructs and returns a GoogleSignInClient.
     */
    val googleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestEmail()
            .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    /**
     * Returns the current [GoogleSignInAccount] if logged in, null otherwise.
     */
    fun accountOrNull(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Returns if the user is logged in or not. Identical to `accountOrNull() != null`.
     */
    fun isLoggedIn(): Boolean {
        return accountOrNull() != null
    }

    /**
     * Sign out. Call if the user logs in with a non-cornell email, or whenever a log out should occur.
     *
     * After calling this, the user will no longer auto-navigate to home, and google auth will
     * correctly query for a new email.
     */
    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    /**
     * Returns an activity result launcher for google login.
     *
     * @param onError The error callback.
     * @param onGoogleSignInCompleted The success callback. Takes in the id token and email.
     */
    @Composable
    fun googleLoginLauncher(
        onError: () -> Unit,
        onGoogleSignInCompleted: (id: String, email: String) -> Unit,
    ): ManagedActivityResultLauncher<Intent, ActivityResult> {
        val scope = rememberCoroutineScope()
        return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                scope.launch {
                    val authResult = firebaseAuth.signInWithCredential(credential).await()
                    Log.d("GoogleAuthRepository", "Success: ${authResult.user!!.uid}")
                    onGoogleSignInCompleted(
                        account.idToken!!,
                        account.email!!
                    )
                }
            } catch (e: ApiException) {
                Log.e("GoogleAuthRepository", "Google sign in failed", e)
                e.printStackTrace()
                onError()
            }
        }
    }

    /**
     * Performs a silent sign in and returns the id token.
     */
    suspend fun silentSignIn(): String {
        val account = googleSignInClient.silentSignIn().await()
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        Log.d("GoogleAuthRepository", "Success: ${authResult.user!!.uid}")

        return account.idToken!!
    }

    suspend fun getOAuthToken(): String = coroutineScope {
        val assetManager = context.assets
        val inputStream = assetManager.open("resell-service.json")
        val credentials = GoogleCredentials
            .fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        CoroutineScope(Dispatchers.IO).launch {
            credentials.refreshIfExpired()
        }.join()

        credentials.accessToken.tokenValue
    }
}
