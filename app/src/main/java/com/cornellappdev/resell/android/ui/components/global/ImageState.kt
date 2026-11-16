package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.ui.graphics.ImageBitmap

/**
 * What a UI component needs to know about an image it is asked to show. Deliberately knows nothing
 * about where the image came from or how it was loaded.
 */
sealed interface ImageState {
    object Loading : ImageState
    object Error : ImageState
    data class Loaded(val bitmap: ImageBitmap) : ImageState
}
