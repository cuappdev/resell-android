package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.onboarding.ResellOnboardingScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.OnboardingNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val onboardingNavigationRepository: OnboardingNavigationRepository,
) : ResellViewModel<SetupViewModel.SetupUiState>(
    initialUiState = SetupUiState()
) {

    data class SetupUiState(
        val username: String = "",
        val bio: String = "",
        val checkedEULA: Boolean = false,
        val errors: List<String> = emptyList(),
        private val loading: Boolean = false,
        val imageBitmap: Bitmap? = null,
        val proceedEvent: UIEvent<Unit>? = null,
    ) {
        val buttonState: ResellTextButtonState
            get() = if (loading) {
                ResellTextButtonState.SPINNING
            } else if (checkedEULA && username.isNotEmpty()) {
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
            copy(username = username)
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
            copy(
                errors = listOf(),
                loading = true
            )
        }

        // TODO testing
        viewModelScope.launch {
            delay(2000)

            // Navigate away.
            onboardingNavigationRepository.navigate(ResellOnboardingScreen.Venmo)

            applyMutation {
                copy(
                    loading = false,
                )
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(appContext, uri)
            if (bitmap != null) {
                applyMutation {
                    copy(imageBitmap = bitmap)
                }
            } else {
                onImageLoadFail()
            }
        }
    }

    fun onImageLoadFail() {
        // TODO
    }
}
