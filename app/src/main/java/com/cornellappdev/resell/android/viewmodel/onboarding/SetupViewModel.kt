package com.cornellappdev.resell.android.viewmodel.onboarding

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor() : ResellViewModel<SetupViewModel.SetupUiState>(
    initialUiState = SetupUiState()
) {

    data class SetupUiState(
        val username: String = "",
        val bio: String = "",
        val checkedEULA: Boolean = false,
        val errors: List<String> = emptyList(),
        private val loading: Boolean = false,
    ) {
        val buttonState: ResellTextButtonState
            get() = if (loading) {
                ResellTextButtonState.SPINNING
            } else if (errors.isEmpty() && checkedEULA && username.isNotEmpty()) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onEULAChanged(checked: Boolean) {
        applyMutation {
            copy(checkedEULA = checked)
        }
    }

    fun onUsernameChanged(username: String) {
        applyMutation {
            copy(username = username, errors = emptyList())
        }
    }

    fun onBioChanged(bio: String) {
        applyMutation {
            copy(bio = bio)
        }
    }

    fun onNextClick() {
        // TODO
        applyMutation {
            copy(loading = true)
        }

        // TODO testing
        viewModelScope.launch {
            delay(2000)
            applyMutation {
                copy(errors = listOf("This username sucks", "it's really bad"), loading = false)
            }
        }
    }

    fun onImageTapped() {
        // TODO
    }
}
