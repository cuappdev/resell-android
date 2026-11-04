package com.cornellappdev.resell.android.model.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A product listing.
 */
@Parcelize
@Serializable
data class Listing(
    val id: String,
    val title: String,
    val images: List<String>,
    val price: String,
    val categories: List<String>,
    val description: String,
    val user: UserInfo
) : Parcelable {
    val image
        get() = images.getOrElse(0) { "" }
}
