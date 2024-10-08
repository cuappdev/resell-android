package com.cornellappdev.resell.android.model

/**
 * A product listing.
 */
data class Listing(
    val id: Int,
    val title: String,
    val image: String,
    val price: String,
    val category: String
)
