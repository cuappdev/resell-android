package com.cornellappdev.resell.android.viewmodel.onboarding
import android.content.Context
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.credentials.exceptions.GetCredentialCancellationException
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val resellAuthRepository: ResellAuthRepository,
) : ResellViewModel<LandingViewModel.LandingUiState>(
    initialUiState = LandingUiState()
) {


    companion object {
        private const val TAG = "LandingViewModel"
    }

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
        val email = googleAuthRepository.signedInEmailOrNull() ?: return
        // Force a token refresh so a revoked session fails here instead of on a later request.
        onSignInCompleted(email = email, forceTokenRefresh = true)
    }

    fun showButton() {
        applyMutation {
            copy(showButton = true)
        }
    }

    /**
     * Shows the Sign in with Google flow.
     *
     * @param activityContext Must be an Activity context; Credential Manager launches UI from it.
     */
    fun onSignInClick(activityContext: Context) {
        applyMutation {
            copy(showButton = true, buttonState = ResellTextButtonState.SPINNING)
        }

        viewModelScope.launch {
            try {
                val profile = googleAuthRepository.signIn(activityContext)
                onSignInCompleted(email = profile.email)
            } catch (e: GetCredentialCancellationException) {
                Log.d(TAG, "Google sign in cancelled.")
                applyMutation {
                    copy(buttonState = ResellTextButtonState.ENABLED)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Google sign in failed", e)
                onSignInFailed(showSheet = true)
            }
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

        viewModelScope.launch { googleAuthRepository.signOut() }
    }

    private fun onSignInCompleted(email: String, forceTokenRefresh: Boolean = false) {
        // Cornell email.
        if (!email.endsWith("@cornell.edu")) {
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
            }

            // No longer logged in.
            viewModelScope.launch { googleAuthRepository.signOut() }
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
                val accessToken = firebaseAuthRepository.getFirebaseAccessToken(
                    forceRefresh = forceTokenRefresh
                )
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
                if (e is HttpException && e.code() == 403) {
                    Log.d(TAG, "User not found on backend; routing to onboarding.")
                    applyMutation {
                        copy(buttonState = ResellTextButtonState.ENABLED)
                    }
                    rootNavigationRepository.navigate(ResellRootRoute.ONBOARDING)
                    return@launch
                }

                Log.e(TAG, "Error getting user: ", e)
                onSignInFailed(showSheet = false)
                rootConfirmationRepository.showError(
                    "Your Google Account session has expired. Please sign in again!",
                )
            }

            delay(500.milliseconds)
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
            }
        }
    }
}
