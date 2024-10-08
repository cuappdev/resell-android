package com.cornellappdev.resell.android.viewmodel.newpost

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.ui.screens.newpost.ResellNewPostScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.NewPostNavigationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageUploadViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    val navigationRepository: NewPostNavigationRepository
) : ResellViewModel<ImageUploadViewModel.ImageUploadUiState>(
    initialUiState = ImageUploadUiState(),
) {

    data class ImageUploadUiState(
        val images: List<ImageBitmap> = emptyList(),
        val launchPhotoPicker: UIEvent<Unit>? = null,
    ) {
        val buttonText
            get() = if (images.isNotEmpty()) "Next" else "Add Image"
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(appContext, uri)?.asImageBitmap()
            if (bitmap != null) {
                applyMutation {
                    copy(images = images.plus(bitmap))
                }
            } else {
                onImageLoadFail()
            }
        }
    }

    fun onAddPressed() {
        applyMutation {
            copy(launchPhotoPicker = UIEvent(Unit))
        }
    }

    fun onDelete(index: Int) {
        applyMutation {
            copy(images = images.minusElement(images[index]))
        }
    }

    fun onButtonPressed() {
        if (uiStateFlow.value.images.isEmpty()) {
            applyMutation {
                copy(launchPhotoPicker = UIEvent(Unit))
            }
        } else {
            navigationRepository.navigate(ResellNewPostScreen.PostDetails)
        }
    }

    fun onImageLoadFail() {
        // TODO
    }
}
