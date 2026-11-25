package com.cornellappdev.resell.android.domain

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ImageState
import com.cornellappdev.resell.android.ui.components.global.ListingUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Pairs listings with their images as the images load, so the UI can show each listing as a
 * [ListingUi].
 */
class GetListingUisUseCase @Inject constructor(
    private val getImageStates: GetImageStatesUseCase,
) {
    /** [listings], in order, each paired with its image as it loads. */
    operator fun invoke(listings: List<Listing>): Flow<List<ListingUi>> =
        getImageStates(listings.map { it.image }).map { images -> listings.withImages(images) }

    /** Like the other [invoke], for listings that may still be loading or have failed to. */
    operator fun invoke(
        response: ResellApiResponse<List<Listing>>,
    ): Flow<ResellApiResponse<List<ListingUi>>> =
        invoke(response.asSuccessOrNull()?.data.orEmpty()).map { listings -> response.map { listings } }
}

/** Pairs each listing with its image from [images], which must have them all. */
fun List<Listing>.withImages(images: Map<String, ImageState>): List<ListingUi> =
    map { ListingUi(it, images.getValue(it.image)) }
