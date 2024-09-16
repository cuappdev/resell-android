package com.cornellappdev.resell.android.viewmodel.onboarding

import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.model.LoginRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LandingViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) :
    ResellViewModel<LandingViewModel.LandingUiState>(
        initialUiState = LandingUiState()
    ) {

    data class LandingUiState(
        val showButton: Boolean = false,
        val buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    )

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
        Log.d("helpme", "login failed")
        applyMutation {
            copy(buttonState = ResellTextButtonState.ENABLED)
        }
    }

    private fun onSignInCompleted(idToken: String, email: String) {
        Log.d("helpme", "login success; idToken: $idToken with email: $email")
        // TODO implement
        applyMutation {
            copy(buttonState = ResellTextButtonState.DISABLED)
        }
    }

    @Composable
    fun makeSignInLauncher(): ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?> {
        return loginRepository.makeActivityResultLauncher(
            onError = this::onSignInFailed,
            onGoogleSignInCompleted = this::onSignInCompleted
        )
    }
}
