package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenmoFieldViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val rootNavigationRepository: RootNavigationRepository,
) : ResellViewModel<VenmoFieldViewModel.VenmoFieldUiState>(
    initialUiState = VenmoFieldUiState()
) {

    data class VenmoFieldUiState(
        val handle: String = "",
        val loading: Boolean = false,
        val proceedMain: UIEvent<Unit>? = null,
    ) {
        // Continue button. Skip is always active.
        val buttonState: ResellTextButtonState
            get() = if (loading) {
                ResellTextButtonState.SPINNING
            } else if (handle.isNotEmpty()) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onContinueClick() {
        // TODO

        // Test
        viewModelScope.launch {
            applyMutation {
                copy(loading = true)
            }
            delay(2000)
            navigateOut()
        }
    }

    fun onSkipClick() {
        navigateOut()
    }

    private fun navigateOut() {
        rootNavigationRepository.navigate(ResellRootRoute.MAIN)

        applyMutation {
            copy(loading = false)
        }
    }

    fun onHandleChanged(handle: String) {
        applyMutation {
            copy(handle = handle)
        }
    }

}
