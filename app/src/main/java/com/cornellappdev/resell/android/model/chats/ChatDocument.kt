package com.cornellappdev.resell.android.model.chats

import com.google.gson.annotations.SerializedName

data class ChatDocument(
    val _id: String,
    val createdAt: String,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: AvailabilityDocument?,
    val product: ProductDocument?
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
