package com.cornellappdev.resell.android.model.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.cornellappdev.resell.android.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
) {
    /**
     * Constructs and returns a GoogleSignInClient.
     */
    private val googleSignInClient: GoogleSignInClient =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestEmail()
            .build()
            .let { gso ->
                GoogleSignIn.getClient(context, gso)
            }

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
        googleSignInClient.signOut()
    }

    /**
     * A contract for google login.
     * @param googleSignInClient The google sign in client.
     */
    class AuthResultContract(private val googleSignInClient: GoogleSignInClient) :
        ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
        override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
            return when (resultCode) {
                Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
                else -> null
            }
        }

        override fun createIntent(context: Context, input: Int): Intent {
            return googleSignInClient.signInIntent.putExtra("input", input)
        }
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
    ): ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?> {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        return rememberLauncherForActivityResult(
            contract = AuthResultContract(googleSignInClient)
        ) {
            try {
                val account = it?.getResult(ApiException::class.java)
                if (account == null || account.idToken == null || account.email == null) {
                    onError()
                } else {
                    coroutineScope.launch {
                        onGoogleSignInCompleted(
                            account.idToken!!,
                            account.email!!
                        )
                    }
                }
            } catch (e: ApiException) {
                onError()
            }
        }
    }
}
