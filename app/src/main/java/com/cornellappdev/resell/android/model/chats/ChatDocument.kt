package com.cornellappdev.resell.android.model.chats

import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatDocument(
    val _id: String,
    val createdAt: Timestamp,
    val image: String,
    val text: String,
    val user: UserDocument,
    val availability: AvailabilityDocument?,
    val product: Post?
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
    val availabilities: List<AvailabilityBlock>
) {
    fun toFirebaseArray(): List<Any> {
        // Sort each availability, sorted by start date:
        val sortedAvailabilities = availabilities.sortedBy { it.startDate }
        return sortedAvailabilities
    }
}

data class AvailabilityBlock(
    val startDate: Timestamp,
    val color: String = ResellPurple.let {
        "#${Integer.toHexString(it.hashCode()).substring(2)}"
    },
    val id: Int,
) {
    val endDate: Timestamp
        get() {
            val instant = startDate.toDate().toInstant()
                .plusSeconds(60 * 30L)
            return Timestamp(instant.epochSecond, instant.nano)
        }
}

data class ProductDocument(
    @SerializedName("_id") val id: String,
)
