package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.RootNav
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenmoFieldViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @RootNav private val navController: NavHostController,
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
        navController.navigate(ResellRootRoute.MAIN)

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
