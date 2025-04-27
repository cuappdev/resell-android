package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.ImageBody
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.onboarding.ResellOnboardingScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.util.toNetworkingString
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
    private val retrofitInstance: RetrofitInstance
) : ResellViewModel<SetupViewModel.SetupUiState>(
    initialUiState = SetupUiState()
) {

    data class SetupUiState(
        val username: String = "",
        val bio: String = "",
        val checkedEULA: Boolean = false,
        val errors: List<String> = emptyList(),
        private val loading: Boolean = false,
        val imageBitmap: ImageBitmap? = null,
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
        applyMutation {
            copy(
                errors = listOf(),
                loading = true
            )
        }

        viewModelScope.launch {
            delay(2000)

            var photoUrl = ""
            if (stateValue().imageBitmap != null) {
                photoUrl = retrofitInstance.userApi.uploadImage(
                    body = ImageBody(
                        imageBase64 = stateValue().imageBitmap!!.toNetworkingString()
                    )
                ).image
            }

            // Navigate away.
            onboardingNavigationRepository.navigate(
                ResellOnboardingScreen.Venmo(
                    username = stateValue().username,
                    bio = stateValue().bio,
                    pfpUrl = photoUrl
                )
            )

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
                    copy(imageBitmap = bitmap.asImageBitmap())
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
