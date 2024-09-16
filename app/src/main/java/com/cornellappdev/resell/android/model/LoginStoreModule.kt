package com.cornellappdev.resell.android.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cornellappdev.resell.android.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

@Module
@InstallIn(SingletonComponent::class) // This ensures it's a singleton across the app
object LoginStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

@Singleton
class LoginRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
) {
    /**
     * Flow for the login state.
     * @return true if the user is known to have been logged in, false otherwise.
     */
    val loginState = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, false)

    /**
     * Writes the login state to DataStore.
     */
    suspend fun saveLoginState(state: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = state
        }
    }

    /**
     * Constructs and returns a GoogleSignInClient.
     */
    private fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
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
    fun makeActivityResultLauncher(
        onError: () -> Unit,
        onGoogleSignInCompleted: (id: String, email: String) -> Unit,
    ): ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?> {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        return rememberLauncherForActivityResult(
            contract = AuthResultContract(getGoogleSignInClient())
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

object PreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}
