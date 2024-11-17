package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.FirebaseAuthRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val fireStoreRepository: FireStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    @ApplicationContext private val context: Context
) : ResellViewModel<LandingViewModel.LandingUiState>(
    initialUiState = LandingUiState()
) {

    data class LandingUiState(
        val showButton: Boolean = false,
        val buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    )

    /**
     * If user is logged in with a valid, finished Resell account, navigate to main.
     * Otherwise does nothing.
     */
    fun navigateIfLoggedIn() {
        if (googleAuthRepository.accountOrNull() != null) {
            onSignInCompleted(
                idToken = googleAuthRepository.accountOrNull()!!.idToken!!,
                email = googleAuthRepository.accountOrNull()!!.email!!
            )
        }
    }

    fun showButton() {
        applyMutation {
            copy(showButton = true)
        }
    }

    fun onSignInClick() {
        applyMutation {
            copy(showButton = true, buttonState = ResellTextButtonState.SPINNING)
        }
    }

    private fun onSignInFailed(
        showSheet: Boolean
    ) {
        applyMutation {
            copy(buttonState = ResellTextButtonState.ENABLED)
        }

        if (showSheet) {
            rootNavigationSheetRepository.showBottomSheet(
                RootSheet.LoginFailed
            )
        }

        googleAuthRepository.signOut()
    }

    private fun onSignInCompleted(idToken: String, email: String) {
        // Cornell email.
        if (!email.endsWith("@cornell.edu")) {
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
            }

            // No longer logged in.
            googleAuthRepository.signOut()
            rootNavigationSheetRepository.showBottomSheet(
                RootSheet.LoginCornellEmail
            )

            return
        }

        viewModelScope.launch {
            try {
                firebaseAuthRepository.firebaseAuthWithGoogle(idToken)

                fireStoreRepository.getUserOnboarded(
                    email = email,
                    onError = {
                        onSignInFailed(showSheet = true)
                    },
                    onSuccess = { onboarded ->
                        viewModelScope.launch {
                            applyMutation {
                                copy(buttonState = ResellTextButtonState.DISABLED)
                            }

                            if (onboarded) {
                                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                            } else {
                                rootNavigationRepository.navigate(ResellRootRoute.ONBOARDING)
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("LandingViewModel", "Error getting user: ", e)
                onSignInFailed(showSheet = false)
                rootConfirmationRepository.showError(
                    "Your Google Account session has expired. Please sign in again!",
                )
            }
        }
    }

    @Composable
    fun makeSignInLauncher(): ManagedActivityResultLauncher<Intent, ActivityResult> {
        return googleAuthRepository.googleLoginLauncher(
            onError = {
                onSignInFailed(showSheet = true)
            },
            onGoogleSignInCompleted = ::onSignInCompleted,
        )
    }

    fun getSignInClient(): GoogleSignInClient {
        val gso = googleAuthRepository.googleSignInClient
        val client = GoogleSignIn.getClient(context, gso)

        return client
    }
}
