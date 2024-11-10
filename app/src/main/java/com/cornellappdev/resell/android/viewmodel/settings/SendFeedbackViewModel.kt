package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
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
class SendFeedbackViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsNavigationRepository: SettingsNavigationRepository,
    private val confirmationRepository: RootConfirmationRepository,
    private val settingsRepository: SettingsRepository
) : ResellViewModel<SendFeedbackViewModel.SendFeedbackUiState>(
    initialUiState = SendFeedbackUiState()
) {
    data class SendFeedbackUiState(
        val feedback: String = "",
        val images: List<ImageBitmap> = emptyList(),
        val loading: Boolean = false,
    ) {
        val canSubmit
            get() = feedback.isNotBlank() && !loading
    }

    fun onImageAdded(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(appContext, uri)?.asImageBitmap()
            if (bitmap != null) {
                applyMutation {
                    copy(images = images.plus(bitmap).take(3))
                }
            } else {
                onImageFailed()
            }
        }
    }

    fun onImageDelete(index: Int) {
        applyMutation {
            copy(images = images.minusElement(images[index]))
        }
    }

    fun onDescriptionChanged(description: String) {
        applyMutation {
            copy(feedback = description)
        }
    }

    fun onImageFailed() {
        confirmationRepository.showError(message = "Something went wrong. Please try again later.")
    }

    fun onFeedbackSubmit() {
        if (stateValue().canSubmit) {
            viewModelScope.launch {
                applyMutation {
                    copy(loading = true)
                }

                try {
                    settingsRepository.sendFeedback(
                        description = stateValue().feedback,
                        images = stateValue().images
                    )
                    confirmationRepository.showSuccess(
                        message = "Your message has been submitted. Thank you for your feedback!",
                    )
                    settingsNavigationRepository.popBackStack()
                } catch (e: Exception) {
                    applyMutation {
                        copy(loading = false)
                    }
                    confirmationRepository.showError(message = "Something went wrong. Please try again later.")
                    Log.e("SendFeedbackViewModel", "Error sending feedback: ", e)
                }
            }
        }
    }
}
