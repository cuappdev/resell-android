package com.cornellappdev.resell.android.model.pdp

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageBitmapLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val cache = mutableMapOf<String, ImageBitmap?>()

    private suspend fun loadBitmap(imageUrl: String): ImageBitmap? {
        // Use coil to load the image
        val imageLoader = ImageLoader(context)

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        // Execute the request asynchronously. Suspends.
        val result = imageLoader.execute(request)
        return if (result is SuccessResult) {
            cache[imageUrl] = result.drawable.toBitmap().asImageBitmap()
            result.drawable.toBitmap().asImageBitmap()
        } else {
            null
        }
    }

    suspend fun getBitmap(imageUrl: String): ImageBitmap? {
        return cache[imageUrl] ?: loadBitmap(imageUrl)
    }
}
