package com.cornellappdev.resell.android.viewmodel.onboarding

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
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
            // TODO: If account actually still exists on backend...
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

        googleAuthRepository.invalidateEmail()
    }

    private fun onSignInCompleted(idToken: String, email: String) {
        // Cornell email.
        if (email.endsWith("@cornell.edu")) {
            viewModelScope.launch {
                googleAuthRepository.saveLoginState(true)
                applyMutation {
                    copy(buttonState = ResellTextButtonState.DISABLED)
                }

                // TODO Should have some logic to check if setup already or not
                rootNavigationRepository.navigate(ResellRootRoute.ONBOARDING)
            }
        }
        // Not a Cornell email.
        else {
            applyMutation {
                copy(buttonState = ResellTextButtonState.ENABLED)
            }

            // No longer logged in.
            googleAuthRepository.invalidateEmail()
            rootNavigationSheetRepository.showBottomSheet(
                RootSheet.LoginCornellEmail
            )
        }

    }

    @Composable
    fun makeSignInLauncher(): ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?> {
        return googleAuthRepository.googleLoginLauncher(
            onError = ::onSignInFailed,
            onGoogleSignInCompleted = ::onSignInCompleted,
        )
    }
}
