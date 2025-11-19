package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.model.classes.DiscountNotifBody
import com.cornellappdev.resell.android.model.classes.ListingRequestNotifBody
import com.cornellappdev.resell.android.model.classes.RecentNotifResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationApiService {
    @GET("notif/recent")
    suspend fun getRecentNotifs(): List<RecentNotifResponse>

    @POST("notif/request-match")
    suspend fun sendRequestMatchNotif(@Body body: ListingRequestNotifBody)

    @POST("notif/discount")
    suspend fun sendDiscountNotification(@Body body: DiscountNotifBody)
}





