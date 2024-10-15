package com.cornellappdev.resell.android.viewmodel.settings

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.screens.settings.SettingsRoute
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.SettingsNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendFeedbackViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsNavigationRepository: SettingsNavigationRepository
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

    }

    fun onFeedbackSubmit() {
        if (stateValue().canSubmit) {
            // TODO
            viewModelScope.launch {
                applyMutation {
                    copy(loading = true)
                }
                delay(2000)

                applyMutation {
                    copy(loading = false)
                }
                settingsNavigationRepository.navigate(SettingsRoute.SettingsLanding)
            }
        }
    }
}
