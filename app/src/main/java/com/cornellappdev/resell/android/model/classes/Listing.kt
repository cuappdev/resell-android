package com.cornellappdev.resell.android.model.classes

import kotlinx.serialization.Serializable

/**
 * A product listing.
 */
@Serializable
data class Listing(
    val id: String,
    val title: String,
    val images: List<String>,
    val price: String,
    val categories: List<String>,
    val description: String,
    val user: UserInfo
) {
    val image
        get() = images[0]
}

/**
 * A request listing.
 */
data class RequestListing(
    val id: String,
    val title: String,
    val description: String,
    val user: UserInfo,
    val matches: List<Listing>
)
