package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.viewmodel.notifications.NotificationType

data class InAppNotif(
    val body: String,
    val timeState: String,
    val data: AdditionalNotifData,
    val id: String,
    val unread: Boolean,
    val title: String,
    val user: User,
    val notificationType: NotificationType
)

data class RecentNotifResponse(
    val body: String,
    val createdAt: String,
    val data: AdditionalNotifData,
    val id: String,
    val read: Boolean,
    val title: String,
    val updatedAt: String,
    val user: User,
    val userId: String
)

data class AdditionalNotifData(
    val postId: String,
    val postTitle: String,
    val price: String,
    val requestId: String,
    val requestTitle: String,
    val sellerId: String,
    val sellerUsername: String
)

fun RecentNotifResponse.toInAppNotif() =
    InAppNotif(
        body = this.body,
        timeState = this.createdAt,
        data = this.data,
        id = this.id,
        unread = !this.read,
        title = this.title,
        user = this.user,
        notificationType = when {
            this.body.contains("discount") -> NotificationType.DISCOUNT
            this.body.contains("message") -> NotificationType.MESSAGE
            this.body.contains("bookmark") -> NotificationType.BOOKMARKS
            this.body.contains("sold") -> NotificationType.SOLD
            this.body.contains("offer") -> NotificationType.OFFER
            this.body.contains("request") -> NotificationType.REQUEST
            else -> NotificationType.OTHER
        }
        //TODO: this is temporary. Will be changed when definitive notif
        // types are added to each object in the API response

    )

data class ListingRequestNotifBody(
    val requestId: String,
    val listingId: String,
    val userId: String
)

data class DiscountNotifBody(
    val sellerId: String,
    val listingId: String,
    val oldPrice: Int,
    val newPrice: Int
)

