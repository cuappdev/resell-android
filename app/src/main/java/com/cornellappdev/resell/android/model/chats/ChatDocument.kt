package com.cornellappdev.resell.android.model.chats

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatDocument(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: AvailabilityDocument?,
    val product: ProductDocument?
)

/**
 * A copy of [ChatDocument] that can be used to push any datatype for availability and product.
 *
 * WARNING: Only use for directly pushing to firebase. Otherwise, use [ChatDocument].
 */
data class ChatDocumentAny(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: Any,
    val product: Any
)

data class UserDocument(
    val _id: String,
    val avatar: String,
    val name: String
)

data class AvailabilityDocument(
    val test: String
)

data class ProductDocument(
    @SerializedName("_id") val id: String,
)
