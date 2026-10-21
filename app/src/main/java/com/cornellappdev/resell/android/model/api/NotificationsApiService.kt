package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.google.gson.annotations.SerializedName
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationsApiService {
    @GET("notif/new")
    suspend fun getNewNotifications(): NotificationsResponse

    @GET("notif/recent")
    suspend fun getRecentNotifications(): NotificationsResponse

    @GET("notif/last7days")
    suspend fun getLast7DaysNotifications(): NotificationsResponse

    @GET("notif/last30days")
    suspend fun getLast30DaysNotifications(): NotificationsResponse

    @POST("notif/read/{id}")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String
    ): SingleNotificationResponse

    @DELETE("notif/id/{id}")
    suspend fun deleteNotification(
        @Path("id") notificationId: String
    )
}

data class NotificationsResponse(
    @SerializedName("notifications") val notifications: List<InAppNotification>? = null,
    @SerializedName("notifs") val notifs: List<InAppNotification>? = null,
) {
    val items: List<InAppNotification>
        get() = notifications ?: notifs ?: emptyList()
}

data class SingleNotificationResponse(
    @SerializedName("notification") val notification: InAppNotification? = null,
    @SerializedName("message") val message: String? = null,
)