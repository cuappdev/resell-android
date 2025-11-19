package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.viewmodel.notifications.NotificationType

data class InAppNotification(
    val body: String,
    val timeState: String,
    val data: AdditionalNotificationData,
    val id: String,
    val unread: Boolean,
    val title: String,
    val user: User,
    val notificationType: NotificationType
)

data class AdditionalNotificationData(
    val postId: String?,
    val postTitle: String?,
    val price: String?,
    val requestId: String?,
    val requestTitle: String?,
    val sellerId: String?,
    val sellerUsername: String?
)


