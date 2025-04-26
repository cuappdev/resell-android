package com.cornellappdev.resell.android.model

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles background image URL loading.
 */
@Singleton
class CoilRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // TODO Make sure this doesn't cause OOM with too many images. Maybe put a size limit?
    private val _urlMapFlow =
        MutableStateFlow<Map<String, ResellApiResponse<ImageBitmap>>>(emptyMap<String, ResellApiResponse<ImageBitmap>>().withDefault { ResellApiResponse.Pending })

    /**
     * Returns a [MutableState] containing an [ResellApiResponse] corresponding to a loading or loaded
     * image bitmap for loading the input [imageUrl]. If the image previously resulted in an error,
     * calling this function will attempt to re-load.
     *
     * Loads images with Coil.
     */
    fun getUrlState(imageUrl: String): Flow<ResellApiResponse<ImageBitmap>> {
        requestImageUrl(imageUrl)

        return _urlMapFlow.map { urlMap ->
            return@map urlMap.getValue(imageUrl)
        }
    }

    fun getImageUrlStates(imageUrls: List<String>): Flow<List<ResellApiResponse<ImageBitmap>>> {
        TODO("Someone could implement this, eventually. It should follow pretty easily from the previous implementation")
    }

    /**
     * Given an image URL, requests the image bitmap, and updates _urlMapFlow accordingly
     * If the image bitmap request was already successful, this function does nothing
     */
    private fun requestImageUrl(imageUrl: String) {
        val imageLoader = context.imageLoader

        if (_urlMapFlow.value.getValue(imageUrl) is ResellApiResponse.Success) {
            return
        }

        // Make the request
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val disposable = imageLoader.enqueue(imageRequest)

        CoroutineScope(Dispatchers.IO).launch {
            val result = disposable.job.await()
            if (result.drawable == null) {
                _urlMapFlow.update {
                    it + (imageUrl to ResellApiResponse.Error)
                }
            } else {
                _urlMapFlow.update {
                    it + (imageUrl to ResellApiResponse.Success(
                        result.drawable!!.toBitmap().asImageBitmap()
                    ))
                }
            }
        }
    }
}
