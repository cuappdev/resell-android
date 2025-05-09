package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.model.profile.ProfileRepository
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
    private val settingsRepository: SettingsRepository,
    private val imageBitmapLoader: ImageBitmapLoader,
    private val profileRepository: ProfileRepository
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
                val response = settingsRepository.editProfile(
                    username = stateValue().username,
                    venmo = stateValue().venmo,
                    bio = stateValue().bio,
                    image = stateValue().imageBitmap?.asImageBitmap()
                )
                settingsNavigationRepository.popBackStack()
                userInfoRepository.storeUserFromUserObject(response.user)
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
        applyMutation {
            copy(
                loading = true
            )
        }
        viewModelScope.launch {
            val userInfo = profileRepository.getUserById(
                userInfoRepository.getUserId() ?: ""
            ).user.toUserInfo()
            val netId = userInfo.netId
            val name = userInfo.name
            val username = userInfo.username
            val venmo = userInfo.venmoHandle
            val bio = userInfo.bio

            applyMutation {
                copy(
                    netId = netId,
                    name = name,
                    username = username,
                    venmo = venmo,
                    bio = bio,
                    loading = false
                )
            }
        }

        // Load current PFP. Should this be done in the same coroutine? Idk.
        viewModelScope.launch {
            val userInfo = profileRepository.getUserById(
                userInfoRepository.getUserId() ?: ""
            ).user.toUserInfo()
            val pfp = userInfo.imageUrl

            val bitmap = imageBitmapLoader.getBitmap(pfp)
            if (bitmap != null) {
                applyMutation {
                    copy(imageBitmap = bitmap.asAndroidBitmap())
                }
            }
        }
    }
}
