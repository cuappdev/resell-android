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
    @SerializedName("data") val data: AdditionalNotificationData = AdditionalNotificationData(),
    @SerializedName("read") val read: Boolean = false,
    @SerializedName("createdAt") private val createdAtRaw: String? = null,
    @SerializedName("updatedAt") private val updatedAtRaw: String? = null,
    @SerializedName("user", alternate = ["sender"]) val user: User? = null,
    @SerializedName("type", alternate = ["notificationType"]) private val typeRaw: String? = null,
) {
    val unread: Boolean
        get() = !read

    val notificationType: NotificationType
        get() {
            return when (typeRaw?.uppercase()) {
                "MESSAGE", "CHAT", "MESSAGES" -> NotificationType.MESSAGE
                "REQUEST", "REQUESTS" -> NotificationType.REQUEST
                "DISCOUNT", "DISCOUNTS" -> NotificationType.DISCOUNT
                "BOOKMARKS", "BOOKMARK" -> NotificationType.BOOKMARKS
                "OFFER", "OFFERS" -> NotificationType.OFFER
                "SOLD" -> NotificationType.SOLD
                else -> NotificationType.OTHER
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

data class AdditionalNotificationData(
    @SerializedName("type") val type: String? = null,
    @SerializedName("notificationType") val notificationType: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("postTitle") val postTitle: String? = null,
    @SerializedName("chatId") val chatId: String? = null,
    @SerializedName("sellerId") val sellerId: String? = null,
    @SerializedName("sellerUsername") val sellerUsername: String? = null,
    @SerializedName("sellerPhotoUrl") val sellerPhotoUrl: String? = null,
    @SerializedName("buyerId") val buyerId: String? = null,
    @SerializedName("buyerUsername") val buyerUsername: String? = null,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("price") val price: Double? = null,
) {
    val resolvedType: String
        get() = (type ?: notificationType ?: "general").lowercase()

    val avatarUrl: String
        get() = imageUrl ?: sellerPhotoUrl ?: ""
}