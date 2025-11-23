package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.DiscountNotificationBody
import com.cornellappdev.resell.android.model.classes.RecentNotifResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationApiService {
    @GET("notif/recent")
    suspend fun getRecentNotifications(): List<RecentNotifResponse>

    @POST("notif/discount")
    suspend fun sendDiscountNotification(@Body body: DiscountNotificationBody)
}





