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

data class AdditionalNotifData(
    val postId: String,
    val postTitle: String,
    val price: String,
    val requestId: String,
    val requestTitle: String,
    val sellerId: String,
    val sellerUsername: String
)


