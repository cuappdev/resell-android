package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.viewmodel.notifications.NotificationType
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class InAppNotification(
    @SerializedName("id", alternate = ["_id"]) val id: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("body") val body: String = "",
    @SerializedName("data") private val _data: AdditionalNotificationData? = AdditionalNotificationData(),
    @SerializedName("read") val read: Boolean = false,
    @SerializedName("createdAt") private val createdAtRaw: String? = null,
    @SerializedName("updatedAt") private val updatedAtRaw: String? = null,
    @SerializedName("user", alternate = ["sender", "senderUser", "fromUser"]) val user: User? = null,
    @SerializedName("type", alternate = ["notificationType"]) private val typeRaw: String? = null,
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("imageUrl", alternate = ["postImageUrl", "postImage", "itemImageUrl", "listingImage", "image", "photoUrl", "picture", "url"]) val rootImageUrl: String? = null,
    @SerializedName("images", alternate = ["postImages", "itemImages", "listingImages"]) val rootImages: List<String>? = null,
    @SerializedName("post", alternate = ["listing", "item"]) val rootPost: NotificationPostData? = null,
    val fetchedImageUrl: String? = null,
) {
    val data: AdditionalNotificationData
        get() = _data ?: AdditionalNotificationData()
    val unread: Boolean
        get() = !read

    val notificationType: NotificationType
        get() {
            val raw = (typeRaw ?: data.type ?: data.notificationType ?: data.resolvedType).uppercase()
            val text = (title + " " + body).uppercase()
            return when {
                raw.contains("MESSAGE") || raw.contains("CHAT") || raw.contains("MSG") ||
                text.contains("MESSAGE") || text.contains("CHAT") -> NotificationType.MESSAGE

                raw.contains("BOOKMARK") || raw.contains("SAVE") || raw.contains("FAV") ||
                text.contains("BOOKMARK") || text.contains("SAVED") || text.contains("SAVE") ||
                text.contains("FAVORITE") || text.contains("FAVOURITE") || text.contains("LIKED") -> NotificationType.BOOKMARKS

                raw.contains("OFFER") || text.contains("OFFER") || text.contains("BID") -> NotificationType.OFFER

                raw.contains("SOLD") || raw.contains("PURCHASE") || raw.contains("BOUGHT") ||
                text.contains("SOLD") || text.contains("PURCHASE") || text.contains("BOUGHT") ||
                text.contains("ORDER") -> NotificationType.SOLD

                else -> NotificationType.OTHER
            }
        }

    val senderProfilePicUrl: String
        get() {
            return data.sellerPhotoUrl?.ifBlank { null }
                ?: data.buyerPhotoUrl?.ifBlank { null }
                ?: user?.photoUrl?.ifBlank { null }
                ?: ""
        }

    val itemImageUrl: String
        get() {
            return fetchedImageUrl?.ifBlank { null }
                ?: rootImageUrl?.ifBlank { null }
                ?: rootImages?.firstOrNull { it.isNotBlank() }
                ?: rootPost?.imageUrl?.ifBlank { null }
                ?: rootPost?.images?.firstOrNull { it.isNotBlank() }
                ?: data.imageUrl?.ifBlank { null }
                ?: data.images?.firstOrNull { it.isNotBlank() }
                ?: data.post?.imageUrl?.ifBlank { null }
                ?: data.post?.images?.firstOrNull { it.isNotBlank() }
                ?: ""
        }

    val displayImageUrl: String
        get() {
            return if (notificationType == NotificationType.MESSAGE) {
                // Messages from other people -> Profile picture of the person who sent the message
                senderProfilePicUrl.ifBlank { itemImageUrl }
            } else {
                // All other notifications -> Image of the actual item/listing (never show user profile picture)
                itemImageUrl
            }
        }

    val createdInstant: Instant
        get() {
            if (createdAtRaw.isNullOrBlank()) return Instant.now()
            return try {
                ZonedDateTime.parse(createdAtRaw, DateTimeFormatter.ISO_DATE_TIME).toInstant()
            } catch (e: Exception) {
                try {
                    val numeric = createdAtRaw.toDouble().toLong()
                    if (numeric > 10_000_000_000L) Instant.ofEpochMilli(numeric)
                    else Instant.ofEpochSecond(numeric)
                } catch (e2: Exception) {
                    Instant.now()
                }
            }
        }
}

data class NotificationPostData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("imageUrl", alternate = ["image", "photoUrl", "picture"]) val imageUrl: String? = null,
)

data class AdditionalNotificationData(
    @SerializedName("type") val type: String? = null,
    @SerializedName("notificationType") val notificationType: String? = null,
    @SerializedName("post") val post: NotificationPostData? = null,
    @SerializedName("imageUrl", alternate = ["postImageUrl", "postImage", "itemImageUrl", "listingImage", "image", "photoUrl", "picture"]) val imageUrl: String? = null,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("postTitle") val postTitle: String? = null,
    @SerializedName("chatId") val chatId: String? = null,
    @SerializedName("sellerId") val sellerId: String? = null,
    @SerializedName("sellerUsername") val sellerUsername: String? = null,
    @SerializedName("sellerPhotoUrl", alternate = ["sellerImage", "senderPhotoUrl", "userPhotoUrl", "profilePicUrl", "senderPfp", "pfp", "avatar"]) val sellerPhotoUrl: String? = null,
    @SerializedName("buyerId") val buyerId: String? = null,
    @SerializedName("buyerUsername") val buyerUsername: String? = null,
    @SerializedName("buyerPhotoUrl", alternate = ["buyerImage", "buyerPfp"]) val buyerPhotoUrl: String? = null,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("price") val price: Double? = null,
) {
    val resolvedType: String
        get() = (type ?: notificationType ?: "general").lowercase()

    val avatarUrl: String
        get() = imageUrl ?: sellerPhotoUrl ?: ""
}