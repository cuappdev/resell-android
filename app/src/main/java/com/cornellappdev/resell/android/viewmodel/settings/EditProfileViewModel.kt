package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsNavigationRepository: SettingsNavigationRepository,
) :
    ResellViewModel<EditProfileViewModel.EditProfileUiState>
        (
        initialUiState = EditProfileUiState()
    ) {
    data class EditProfileUiState(
        val netId: String = "",
        val name: String = "",
        val username: String = "",
        val venmo: String = "",
        val bio: String = "",
        val imageBitmap: Bitmap? = null,
        private val loading: Boolean = false,
    ) {
        val canSubmit
            get() = username.isNotBlank() && !loading
    }

    fun onUsernameChanged(username: String) {
        applyMutation {
            copy(username = username)
        }
    }

    fun onVenmoHandleChanged(venmo: String) {
        applyMutation {
            copy(venmo = venmo)
        }
    }

    fun onBioChanged(bio: String) {
        applyMutation {
            copy(bio = bio)
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

    fun onSubmit() {
        applyMutation {
            copy(loading = true)
        }

        // TODO: Save.
        viewModelScope.launch {
            delay(2000)
            settingsNavigationRepository.popBackStack()
            applyMutation {
                copy(loading = false)
            }
        }
    }

    init {
        // TODO: Get user info
        applyMutation {
            copy(
                netId = "temp netId",
                name = "temp name",
                username = "temp username",
                venmo = "temp venmo",
                bio = "temp bio",
            )
        }
    }
}
