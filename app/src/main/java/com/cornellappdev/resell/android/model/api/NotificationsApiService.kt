package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationsApiService {
    @GET("notif/new")
    suspend fun getNewNotifications(): List<InAppNotification>

    @GET("notif/recent")
    suspend fun getRecentNotifications(): List<InAppNotification>

    @GET("notif/last7days")
    suspend fun getLast7DaysNotifications(): List<InAppNotification>

    @GET("notif/last30days")
    suspend fun getLast30DaysNotifications(): List<InAppNotification>

    @POST("notif/read/{id}")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String
    ): InAppNotification

    @DELETE("notif/id/{id}")
    suspend fun deleteNotification(
        @Path("id") notificationId: String
    )
}