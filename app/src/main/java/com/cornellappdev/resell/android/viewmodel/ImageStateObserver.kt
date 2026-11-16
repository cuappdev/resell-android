package com.cornellappdev.resell.android.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ImageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Loads a set of image URLs through [coilRepository] and reports the latest [ImageState] of each
 * one, keyed by URL, through [onUpdate] (typically copying it into a ViewModel's UI state).
 *
 * Call [observe] every time the set of URLs on screen changes; it replaces the previous set.
 */
class ImageStateObserver(
    private val coilRepository: CoilRepository,
    private val scope: CoroutineScope,
    private val onUpdate: (Map<String, ImageState>) -> Unit,
) {
    private var job: Job? = null

    fun observe(urls: Collection<String>) {
        job?.cancel()
        // Requesting the images has side effects (it kicks off loads), so do it exactly once here
        // rather than inside the snapshotFlow below, which re-runs on every change.
        val states = urls.distinct().associateWith { coilRepository.getUrlState(it) }
        job = scope.launch {
            snapshotFlow { states.mapValues { (_, state) -> state.value.toImageState() } }
                .collect(onUpdate)
        }
    }
}

private fun ResellApiResponse<ImageBitmap>.toImageState(): ImageState = when (this) {
    ResellApiResponse.Pending -> ImageState.Loading
    ResellApiResponse.Error -> ImageState.Error
    is ResellApiResponse.Success -> ImageState.Loaded(data)
}
