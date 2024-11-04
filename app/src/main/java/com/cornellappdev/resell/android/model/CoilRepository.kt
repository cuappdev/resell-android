package com.cornellappdev.resell.android.model

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val urlMap: MutableMap<String, MutableState<ResellApiResponse<ImageBitmap>>> =
        mutableMapOf()

    /**
     * Returns a [MutableState] containing an [ResellApiResponse] corresponding to a loading or loaded
     * image bitmap for loading the input [imageUrl]. If the image previously resulted in an error,
     * calling this function will attempt to re-load.
     *
     * Loads images with Coil.
     */
    fun getUrlState(imageUrl: String): MutableState<ResellApiResponse<ImageBitmap>> {
        val imageLoader = context.imageLoader

        // Make new request.
        if (!urlMap.containsKey(imageUrl) || urlMap[imageUrl]!!.value is ResellApiResponse.Error) {
            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            urlMap[imageUrl] = mutableStateOf(ResellApiResponse.Pending)

            val disposable = imageLoader.enqueue(imageRequest)

            CoroutineScope(Dispatchers.IO).launch {
                val result = disposable.job.await()
                if (result.drawable == null) {
                    urlMap[imageUrl]!!.value = ResellApiResponse.Error
                } else {
                    urlMap[imageUrl]!!.value =
                        ResellApiResponse.Success(result.drawable!!.toBitmap().asImageBitmap())
                }
            }
        }

        return urlMap[imageUrl]!!
    }
}
