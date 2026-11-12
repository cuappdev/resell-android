package com.cornellappdev.resell.android.model.notifications

import android.util.Log
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {
    suspend fun getLast30DaysNotifications(): ResellApiResponse<List<InAppNotification>> {
        return try {
            val response = retrofitInstance.notificationsApi.getLast30DaysNotifications()
            val deduplicated = deduplicate(response.items)
            val enriched = enrichNotificationsWithImages(deduplicated)
            ResellApiResponse.Success(enriched)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error fetching 30 days notifications: ", e)
            ResellApiResponse.Error
        }
    }

    suspend fun getNewNotifications(): ResellApiResponse<List<InAppNotification>> {
        return try {
            val response = retrofitInstance.notificationsApi.getNewNotifications()
            val deduplicated = deduplicate(response.items)
            val enriched = enrichNotificationsWithImages(deduplicated)
            ResellApiResponse.Success(enriched)
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

    private suspend fun enrichNotificationsWithImages(list: List<InAppNotification>): List<InAppNotification> {
        return coroutineScope {
            list.map { notif ->
                async {
                    val targetPostId = notif.data.postId?.ifBlank { null } ?: notif.postId?.ifBlank { null }
                    if (notif.itemImageUrl.isBlank() && !targetPostId.isNullOrBlank()) {
                        try {
                            val postResponse = retrofitInstance.postsApi.getPost(targetPostId)
                            val firstImage = postResponse.post.images.firstOrNull { it.isNotBlank() }
                            if (!firstImage.isNullOrBlank()) {
                                notif.copy(fetchedImageUrl = firstImage)
                            } else {
                                notif
                            }
                        } catch (e: Exception) {
                            Log.e("NotificationRepository", "Failed to fetch post for postId: $targetPostId", e)
                            notif
                        }
                    } else {
                        notif
                    }
                }
            }.awaitAll()
        }
    }

    private fun deduplicate(list: List<InAppNotification>): List<InAppNotification> {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<InAppNotification>()
        val sorted = list.sortedByDescending { it.createdInstant }

        for (notif in sorted) {
            val type = notif.data.resolvedType
            val postId = notif.data.postId ?: notif.postId ?: ""
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