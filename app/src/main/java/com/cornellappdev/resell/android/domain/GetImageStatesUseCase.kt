package com.cornellappdev.resell.android.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.NetworkMonitor
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ImageState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Loads images by URL for the UI, through [CoilRepository].
 */
class GetImageStatesUseCase @Inject constructor(
    private val coilRepository: CoilRepository,
    private val networkMonitor: NetworkMonitor,
) {
    /**
     * The [ImageState] of each of [urls], keyed by URL, updating as the images load. Any that
     * failed are retried whenever the device's connection changes.
     */
    operator fun invoke(urls: Collection<String>): Flow<Map<String, ImageState>> =
        networkMonitor.isOnline.flatMapLatest { imageStates(urls) }

    /**
     * The images are only requested once the flow is collected, so collecting it again retries
     * any that failed.
     */
    private fun imageStates(urls: Collection<String>): Flow<Map<String, ImageState>> = flow {
        val distinctUrls = urls.distinct()
        // combine never emits when given no flows.
        if (distinctUrls.isEmpty()) {
            emit(emptyMap())
            return@flow
        }
        emitAll(combine(distinctUrls.map { coilRepository.imageState(it) }) { responses ->
            distinctUrls.zip(responses) { url, response -> url to response.toImageState() }.toMap()
        })
    }
}

private fun ResellApiResponse<ImageBitmap>.toImageState(): ImageState = when (this) {
    ResellApiResponse.Pending -> ImageState.Loading
    ResellApiResponse.Error -> ImageState.Error
    is ResellApiResponse.Success -> ImageState.Loaded(data)
}
