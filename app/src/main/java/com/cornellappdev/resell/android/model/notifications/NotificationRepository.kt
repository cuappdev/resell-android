package com.cornellappdev.resell.android.model.notifications

import android.util.Log
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getLast30DaysNotifications(): ResellApiResponse<List<InAppNotification>> {
        return try {
            val response = retrofitInstance.notificationsApi.getLast30DaysNotifications()
            ResellApiResponse.Success(deduplicate(response.items))
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error fetching 30 days notifications: ", e)
            ResellApiResponse.Error
        }
    }

    suspend fun getNewNotifications(): ResellApiResponse<List<InAppNotification>> {
        return try {
            val response = retrofitInstance.notificationsApi.getNewNotifications()
            ResellApiResponse.Success(deduplicate(response.items))
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error fetching new notifications: ", e)
            ResellApiResponse.Error
        }
    }

    suspend fun markAsRead(notificationId: String): ResellApiResponse<InAppNotification> {
        return try {
            val response = retrofitInstance.notificationsApi.markNotificationAsRead(notificationId)
            val notif = response.notification
            if (notif != null) {
                ResellApiResponse.Success(notif)
            } else {
                ResellApiResponse.Error
            }
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error marking notification read: ", e)
            ResellApiResponse.Error
        }
    }

    suspend fun deleteNotification(notificationId: String): ResellApiResponse<Unit> {
        return try {
            retrofitInstance.notificationsApi.deleteNotification(notificationId)
            ResellApiResponse.Success(Unit)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error deleting notification: ", e)
            ResellApiResponse.Error
        }
    }

    private fun deduplicate(list: List<InAppNotification>): List<InAppNotification> {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<InAppNotification>()
        val sorted = list.sortedByDescending { it.createdInstant }

        for (notif in sorted) {
            val type = notif.data.resolvedType
            val postId = notif.data.postId ?: ""
            val senderId = notif.data.buyerId ?: notif.data.sellerId ?: ""
            val timeWindow = notif.createdInstant.epochSecond / 60
            val key = "${type}_${postId}_${senderId}_${timeWindow}"

            if (seen.add(key)) {
                result.add(notif)
            }
        }
        return result
    }
}