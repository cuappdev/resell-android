package com.cornellappdev.resell.android.model.notifications

import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getNewNotifications(): ResellApiResponse<List<InAppNotification>> {
        return try {
            val notifs = retrofitInstance.notificationsApi.getNewNotifications()
            ResellApiResponse.Success(notifs)
        } catch (e: Exception) {
            ResellApiResponse.Error
        }
    }

    suspend fun markAsRead(notificationId: String): ResellApiResponse<InAppNotification> {
        return try {
            val notif = retrofitInstance.notificationsApi.markNotificationAsRead(notificationId)
            ResellApiResponse.Success(notif)
        } catch (e: Exception) {
            ResellApiResponse.Error
        }
    }

    suspend fun deleteNotification(notificationId: String): ResellApiResponse<Unit> {
        return try {
            retrofitInstance.notificationsApi.deleteNotification(notificationId)
            ResellApiResponse.Success(Unit)
        } catch (e: Exception) {
            ResellApiResponse.Error
        }
    }
}