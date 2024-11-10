package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.settings.SettingsRepository
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsNavigationRepository: SettingsNavigationRepository,
    private val confirmationRepository: RootConfirmationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val settingsRepository: SettingsRepository
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

        viewModelScope.launch {
            try {
                settingsRepository.editProfile(
                    username = stateValue().username,
                    venmo = stateValue().venmo,
                    bio = stateValue().bio,
                    image = stateValue().imageBitmap?.asImageBitmap()
                )
                settingsNavigationRepository.popBackStack()
                confirmationRepository.showSuccess("Profile updated successfully!")
            } catch (e: Exception) {
                applyMutation {
                    copy(loading = false)
                }
                confirmationRepository.showError()
                Log.e("EditProfileViewModel", "Error editing profile: ", e)
            }

        }
    }

    init {
        viewModelScope.launch {
            val netid = userInfoRepository.getUserInfo().netId
            val name = userInfoRepository.getUserInfo().name
            val username = userInfoRepository.getUserInfo().username
            val venmo = userInfoRepository.getUserInfo().venmoHandle
            val bio = userInfoRepository.getUserInfo().bio

            applyMutation {
                copy(
                    netId = netid,
                    name = name,
                    username = username,
                    venmo = venmo,
                    bio = bio
                )
            }
        }
    }
}
