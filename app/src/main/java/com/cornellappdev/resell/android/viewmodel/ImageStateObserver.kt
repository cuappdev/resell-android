package com.cornellappdev.resell.android.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.ImageBitmap
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ImageState
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Loads a set of image URLs through [coilRepository] and reports a value of type [T] built from the
 * latest [ImageState] of each one through [onUpdate] (typically copying it into a ViewModel's UI
 * state). [onUpdate] is called immediately with the current state of the images, and again every
 * time one of them changes.
 *
 * Call [observe] (or [observeListings]) every time the set of items on screen changes; it replaces
 * the previous set.
 */
class ImageStateObserver<T>(
    private val coilRepository: CoilRepository,
    private val scope: CoroutineScope,
    private val onUpdate: (T) -> Unit,
) {
    private var job: Job? = null

    /**
     * Observes [urls], and reports whatever [build] returns. [build] is handed a function that
     * looks up the current [ImageState] of any of [urls].
     */
    fun observe(urls: Collection<String>, build: (image: (String) -> ImageState) -> T) {
        job?.cancel()
        // Requesting the images has side effects (it kicks off loads), so do it exactly once here
        // rather than inside the snapshotFlow below, which re-runs on every change.
        val states = urls.distinct().associateWith { coilRepository.getUrlState(it) }
        val imageOf: (String) -> ImageState = { url ->
            checkNotNull(states[url]) { "Image $url is not being observed" }.value.toImageState()
        }
        onUpdate(build(imageOf))
        job = scope.launch {
            snapshotFlow { build(imageOf) }.collect(onUpdate)
        }
    }

    /** Stops observing and reports [value] instead */
    fun stopWith(value: T) {
        job?.cancel()
        job = null
        onUpdate(value)
    }
}

fun <T> ImageStateObserver<T>.observeListings(
    listings: List<Listing>,
    build: (List<ListingUi>) -> T,
) = observe(listings.map { it.image }) { image ->
    build(listings.map { ListingUi(it, image(it.image)) })
}

fun ImageStateObserver<List<ListingUi>>.observeListings(listings: List<Listing>) =
    observeListings(listings) { it }

fun ImageStateObserver<ResellApiResponse<List<ListingUi>>>.observeListingResponse(
    response: ResellApiResponse<List<Listing>>,
) = when (response) {
    is ResellApiResponse.Success -> observeListings(response.data) { ResellApiResponse.Success(it) }
    ResellApiResponse.Pending -> stopWith(ResellApiResponse.Pending)
    ResellApiResponse.Error -> stopWith(ResellApiResponse.Error)
}

private fun ResellApiResponse<ImageBitmap>.toImageState(): ImageState = when (this) {
    ResellApiResponse.Pending -> ImageState.Loading
    ResellApiResponse.Error -> ImageState.Error
    is ResellApiResponse.Success -> ImageState.Loaded(data)
}
