package com.cornellappdev.resell.android.model.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FcmApiService {

    @POST("v1/projects/resell-e99a2/messages:send")
    suspend fun sendNotification(
        @Body body: FcmBody,
        @Header("Authorization") authToken: String,
        @Header("Accept") accept: String = "application/json",
    )
}

data class FcmBody(
    val message: FcmMessage
)

data class FcmMessage(
    val token: String,
    val notification: FcmNotification?,
    val data: NotificationData
)

data class FcmNotification(
    val title: String,
    val body: String
)

data class NotificationData(
    val navigationId: String,
    // TODO: No idea what this class is
//    val chat: BuyerSellerData
)
