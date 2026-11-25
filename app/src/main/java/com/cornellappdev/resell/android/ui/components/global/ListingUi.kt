package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.runtime.Immutable
import com.cornellappdev.resell.android.model.classes.Listing

/**
 * A [Listing] together with the state of its image, so every listing in the UI carries what to
 * render for its picture. The ViewModel keeps [image] up to date as the image loads.
 */
@Immutable
data class ListingUi(
    val listing: Listing,
    val image: ImageState,
) {
    val id: String
        get() = listing.id
}
