package com.cornellappdev.resell.android.model

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
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
    private val urlMap =
        ConcurrentHashMap<String, MutableStateFlow<ResellApiResponse<ImageBitmap>>>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Returns a [StateFlow] of an [ResellApiResponse] corresponding to a loading or loaded image
     * bitmap for the input [imageUrl], starting the load if it hasn't been started. If the image
     * previously resulted in an error, calling this function will attempt to re-load.
     *
     * Loads images with Coil.
     */
    fun imageState(imageUrl: String): StateFlow<ResellApiResponse<ImageBitmap>> {
        // A new image starts out as an error, so that loading it is the same as retrying it.
        val state = urlMap.getOrPut(imageUrl) { MutableStateFlow(ResellApiResponse.Error) }
        // compareAndSet so that only one of several callers starts the load.
        if (state.compareAndSet(ResellApiResponse.Error, ResellApiResponse.Pending)) {
            load(imageUrl, state)
        }
        return state.asStateFlow()
    }

    private fun load(imageUrl: String, state: MutableStateFlow<ResellApiResponse<ImageBitmap>>) {
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        scope.launch {
            val drawable = context.imageLoader.execute(imageRequest).drawable
            state.value = if (drawable == null) {
                ResellApiResponse.Error
            } else {
                ResellApiResponse.Success(drawable.toBitmap().asImageBitmap())
            }
        }
    }
}
