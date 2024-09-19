package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.onboarding.SetupViewModel.SetupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class VenmoFieldViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ResellViewModel<VenmoFieldViewModel.VenmoFieldUiState>(
    initialUiState = VenmoFieldUiState()
) {

    data class VenmoFieldUiState(
        val handle: String = "",
    ) {
        // Continue button. Skip is always active.
        val buttonState: ResellTextButtonState
            get() = if (handle.isNotEmpty()) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onContinueClick() {
        // TODO
    }

    fun onSkipClick() {
        // TODO
    }

    fun onHandleChanged(handle: String) {
        applyMutation {
            copy(handle = handle)
        }
    }

}
