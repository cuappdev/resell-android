package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.content.Intent
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
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
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
            rootNavigationRepository.navigate(ResellRootRoute.MAIN)
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

    private fun onSignInFailed() {
        applyMutation {
            copy(buttonState = ResellTextButtonState.ENABLED)
        }

        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.LoginFailed
        )

        googleAuthRepository.signOut()
    }

    private fun onSignInCompleted(idToken: String, email: String) {
        // Cornell email.
        if (email.endsWith("@cornell.edu")) {
            viewModelScope.launch {
                firebaseAuthRepository.firebaseAuthWithGoogle(idToken)

                fireStoreRepository.getUserOnboarded(
                    email = email,
                    onError = {
                        googleAuthRepository.signOut()
                        rootNavigationSheetRepository.showBottomSheet(RootSheet.LoginFailed)
                        applyMutation {
                            copy(buttonState = ResellTextButtonState.ENABLED)
                        }
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
            }
        }
        // Not a Cornell email.
        else {
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
            }

            // No longer logged in.
            googleAuthRepository.signOut()
            rootNavigationSheetRepository.showBottomSheet(
                RootSheet.LoginCornellEmail
            )
        }
    }

    @Composable
    fun makeSignInLauncher(): ManagedActivityResultLauncher<Intent, ActivityResult> {
        return googleAuthRepository.googleLoginLauncher(
            onError = ::onSignInFailed,
            onGoogleSignInCompleted = ::onSignInCompleted,
        )
    }

    fun getSignInClient(): GoogleSignInClient {
        val gso = googleAuthRepository.googleSignInClient
        val client = GoogleSignIn.getClient(context, gso)

        return client
    }
}
