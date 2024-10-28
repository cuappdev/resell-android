package com.cornellappdev.resell.android.model.classes

/**
 * A product listing.
 */
data class Listing(
    val id: String,
    val title: String,
    val image: String,
    val price: String,
    val categories: List<String>
)
