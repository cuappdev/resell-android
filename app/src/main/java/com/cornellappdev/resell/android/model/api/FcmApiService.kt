package com.cornellappdev.resell.android.model.api

import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
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
    val data: Any?,
)

/**
 * The title and body of the notification that will be displayed as a banner.
 */
data class FcmNotification(
    val title: String,
    val body: String
)

/**
 * Custom data we pass to the notification.
 */
sealed class NotificationData {
    open val navigationId: String
        get() {
            return  ""
        }

    data class ChatNotification(
        val name: String,
        val email: String,
        val pfp: String,
        val postJson: String,
        /**
         * "true" if the user receiving this is a buyer, "false" if the user is a seller
         */
        val isBuyer: String
    ) : NotificationData() {
        override val navigationId: String
            get() = "chat"
    }

    // TODO: Add other notifications as needed
}
