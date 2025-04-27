package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FirebaseAuthRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val resellAuthRepository: ResellAuthRepository,
    @ApplicationContext private val context: Context
) : ResellViewModel<LandingViewModel.LandingUiState>(
    initialUiState = LandingUiState()
) {

    data class LandingUiState(
        val showButton: Boolean = false,
        val buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
        val retryLogin: UIEvent<Unit>? = null,
    )

    /**
     * If user is logged in with a valid, finished Resell account, attempts an auto login.
     * Otherwise does nothing.
     */
    fun attemptAutoLogin() {
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
                RootSheet.TwoButtonSheet(
                    title = "Login Failed",
                    description = AnnotatedString("Login failed. Please try again."),
                    primaryText = "Try Again",
                    primaryCallback = {
                        applyMutation {
                            copy(
                                retryLogin = UIEvent(Unit)
                            )
                        }
                        rootNavigationSheetRepository.hideSheet()
                    },
                    secondaryText = "Okay",
                    secondaryCallback = {
                        rootNavigationSheetRepository.hideSheet()
                    },
                    secondaryContainerType = ResellTextButtonContainer.NAKED,
                    textAlign = TextAlign.Center
                )
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
                RootSheet.TwoButtonSheet(
                    title = "Login Failed",
                    description = AnnotatedString("Please log in with a Cornell email."),
                    primaryText = "Try Again",
                    primaryCallback = {
                        applyMutation {
                            copy(
                                retryLogin = UIEvent(Unit)
                            )
                        }
                        rootNavigationSheetRepository.hideSheet()
                    },
                    secondaryText = "Okay",
                    secondaryCallback = {
                        rootNavigationSheetRepository.hideSheet()
                    },
                    secondaryContainerType = ResellTextButtonContainer.NAKED,
                    textAlign = TextAlign.Center
                )
            )

            return
        }

        applyMutation {
            copy(buttonState = ResellTextButtonState.DISABLED)
        }

        // Attempt to log in.
        viewModelScope.launch {
            try {
                val newId = googleAuthRepository.silentSignIn()

                firebaseAuthRepository.firebaseSignIn(newId)
                val accessToken = firebaseAuthRepository.getFirebaseAccessToken()
                if (accessToken == null) {
                    onSignInFailed(showSheet = true)
                    rootConfirmationRepository.showError(
                        "Error connecting to our server. Please try again later."
                    )
                    return@launch
                }

                val user = resellAuthRepository.authenticate()

                if (user == null) {
                    rootNavigationRepository.navigate(ResellRootRoute.ONBOARDING)
                } else {
                    userInfoRepository.storeUserFromUserObject(user)
                    rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                }
            } catch (e: Exception) {
                Log.e("LandingViewModel", "Error getting user: ", e)
                onSignInFailed(showSheet = false)
                rootConfirmationRepository.showError(
                    "Your Google Account session has expired. Please sign in again!",
                )
            }

            delay(500)
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
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
        val gso = googleAuthRepository.googleSignInOptions
        val client = GoogleSignIn.getClient(context, gso)

        return client
    }
}
